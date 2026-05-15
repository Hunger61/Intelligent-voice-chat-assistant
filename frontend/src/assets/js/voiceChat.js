import store from '../../store'
import { getAuthToken } from '../../api/auth'
import { startCallTimer, stopCallTimer } from './timer'
import { showErrMessage } from './showErrOrTrue'

// ---- state ----
let ws = null
let seq = 0
let audioContext = null
let mediaStream = null
let audioTrack = null
let isMicMuted = true
let audioDataCallback = null
let audioIntervals = null
let scriptProcessor = null
let ttsBuffer = [] // queue for incoming TTS audio chunks

const SAMPLE_RATE = 16000
const FRAME_SIZE = 640 // 20ms @ 16kHz = 320 samples, Int16 = 640 bytes

function nextSeq() {
    return ++seq
}

// ============================================================
//  Audio Capture — Mic → PCM Int16 → WS Binary (0x01)
// ============================================================

async function initAudioCapture() {
    try {
        mediaStream = await navigator.mediaDevices.getUserMedia({
            audio: {
                echoCancellation: true,
                noiseSuppression: true,
                autoGainControl: true,
                sampleRate: SAMPLE_RATE,
                channelCount: 1,
                sampleSize: 16
            },
            video: false
        })

        audioContext = new (window.AudioContext || window.webkitAudioContext)({
            sampleRate: SAMPLE_RATE,
            latencyHint: 'interactive'
        })

        const source = audioContext.createMediaStreamSource(mediaStream)
        const analyser = audioContext.createAnalyser()
        analyser.fftSize = 2048
        source.connect(analyser)

        // ScriptProcessor for PCM capture
        scriptProcessor = audioContext.createScriptProcessor(4096, 1, 1)
        source.connect(scriptProcessor)
        scriptProcessor.connect(audioContext.destination)

        scriptProcessor.onaudioprocess = (event) => {
            if (!ws || ws.readyState !== WebSocket.OPEN) return
            if (isMicMuted) return

            const input = event.inputBuffer.getChannelData(0)
            const pcm = float32ToInt16(input)
            const header = new Uint8Array([0x01]) // mic audio
            const frame = new Uint8Array(1 + pcm.byteLength)
            frame.set(header, 0)
            frame.set(new Uint8Array(pcm.buffer), 1)
            ws.send(frame)
        }

        audioTrack = mediaStream.getAudioTracks()[0]
        audioTrack.enabled = false // start muted

        startAudioMonitoring(analyser)
    } catch (err) {
        console.error('Failed to init audio capture:', err)
        showErrMessage('麦克风访问失败')
    }
}

function float32ToInt16(float32) {
    const buf = new Int16Array(float32.length)
    for (let i = 0; i < float32.length; i++) {
        const s = Math.max(-1, Math.min(1, float32[i]))
        buf[i] = s < 0 ? s * 0x8000 : s * 0x7FFF
    }
    return buf
}

function startAudioMonitoring(analyser) {
    if (!analyser) return
    const bufferLength = analyser.frequencyBinCount
    const timeDomainData = new Uint8Array(bufferLength)
    const frequencyData = new Uint8Array(bufferLength)

    audioIntervals = setInterval(() => {
        if (!store.getters.getIsCallActive) return

        if (isMicMuted || !audioTrack?.enabled) {
            if (audioDataCallback) audioDataCallback({ volume: 0, fftData: new Uint8Array(bufferLength), sampleRate: SAMPLE_RATE })
            return
        }

        analyser.getByteTimeDomainData(timeDomainData)
        analyser.getByteFrequencyData(frequencyData)

        let sum = 0
        for (let i = 0; i < bufferLength; i++) {
            const value = (timeDomainData[i] - 128) / 128
            sum += value * value
        }
        const volume = Math.sqrt(sum / bufferLength)

        if (audioDataCallback) {
            audioDataCallback({ volume, fftData: frequencyData, sampleRate: SAMPLE_RATE })
        }
    }, 100)
}

export function setAudioDataCallback(callback) {
    audioDataCallback = callback
}

export function toggleMicrophone() {
    if (!audioTrack) return true
    isMicMuted = !isMicMuted
    audioTrack.enabled = !isMicMuted
    return isMicMuted
}

// ============================================================
//  Audio Playback — WS Binary (0x02) → PCM Int16 → Speaker
// ============================================================

function playAudioChunk(pcmInt16) {
    if (!audioContext) return

    const float32 = int16ToFloat32(pcmInt16)
    const buffer = audioContext.createBuffer(1, float32.length, SAMPLE_RATE)
    buffer.getChannelData(0).set(float32)

    const source = audioContext.createBufferSource()
    source.buffer = buffer
    source.connect(audioContext.destination)
    source.start()
}

function int16ToFloat32(int16) {
    const float32 = new Float32Array(int16.length)
    for (let i = 0; i < int16.length; i++) {
        float32[i] = int16[i] / (int16[i] < 0 ? 0x8000 : 0x7FFF)
    }
    return float32
}

// ============================================================
//  WebSocket lifecycle
// ============================================================

export async function startVoiceChat() {
    if (ws && ws.readyState === WebSocket.OPEN) return

    const token = getAuthToken()
    if (!token) {
        showErrMessage('未登录，无法发起语音')
        return
    }

    store.commit('setIsCallActive', true)

    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
    const wsUrl = `${protocol}//${location.host}/ws/speech?token=${token}`
    ws = new WebSocket(wsUrl)
    ws.binaryType = 'arraybuffer'

    ws.onopen = () => {
        store.commit('setPipelineState', 'IDLE')
        startCallTimer()

        ws.send(JSON.stringify({
            command: 'voice.session.join',
            seq: nextSeq(),
            payload: {
                aiAssistantId: String(store.getters.getSelectedId),
                tts: {
                    speaker: store.getters.getSpeaker
                }
            }
        }))

        initAudioCapture()
    }

    ws.onmessage = (event) => {
        if (event.data instanceof ArrayBuffer) {
            handleBinaryFrame(event.data)
        } else {
            handleTextFrame(event.data)
        }
    }

    ws.onclose = (event) => {
        console.log('Voice WS closed:', event.reason)
        cleanup()
    }

    ws.onerror = () => {
        showErrMessage('语音连接失败')
        cleanup()
    }
}

function handleTextFrame(data) {
    let msg
    try {
        msg = JSON.parse(data)
    } catch {
        return
    }

    const eventType = msg.event
    const payload = msg.payload
    if (!eventType) return

    switch (eventType) {
        case 'voice.session.started':
            store.commit('setPipelineState', 'IDLE')
            break

        case 'voice.session.ended':
            cleanup()
            break

        case 'voice.asr.interim':
            store.commit('setAsrInterimText', payload.text)
            break

        case 'voice.asr.final':
            store.commit('setAsrInterimText', '')
            store.commit('addnewMessagebyId', {
                message: {
                    assistantId: store.getters.getSelectedId,
                    role: 'user',
                    content: payload.text
                },
                id: store.getters.getSelectedId
            })
            store.commit('setIsHaveNewMessage', true)
            break

        case 'voice.llm.token':
            store.commit('appendLlmToken', payload.token)
            break

        case 'voice.llm.thinking':
            store.commit('appendLlmThinking', payload.token)
            break

        case 'voice.llm.complete':
            store.commit('finalizeLlmMessage', {
                fullText: payload.fullText,
                chatId: payload.chatId,
                dialogueId: payload.dialogueId
            })
            break

        case 'voice.tts.started':
            store.commit('setTtsPlaying', true)
            break

        case 'voice.tts.ended':
            store.commit('setTtsPlaying', false)
            store.commit('setIsHaveNewMessage', false)
            break

        case 'voice.tts.interrupted':
            store.commit('setTtsPlaying', false)
            store.commit('setIsHaveNewMessage', false)
            break

        case 'voice.pipeline.state':
            store.commit('setPipelineState', payload.state)
            break

        case 'voice.error':
            console.warn('Voice error:', payload.code, payload.message)
            showErrMessage(payload.message)
            if (!payload.recoverable) {
                cleanup()
            }
            break

        // ---- agent events (voice channel) ----
        case 'agent_thinking':
            store.commit('setIsAgentActive', true)
            store.commit('addAgentStep', { type: 'thinking', content: payload.text })
            break

        case 'agent_tool_call':
            store.commit('addAgentStep', { type: 'tool_call', tool: payload.tool, args: payload.args })
            break

        case 'agent_tool_result':
            store.commit('updateLastAgentStep', { type: 'tool_result', result: payload.summary, success: payload.success !== false })
            break

        case 'agent_complete':
            store.commit('setIsAgentActive', false)
            break

        // ---- reactive events ----
        case 'reactive_event':
            handleReactiveEvent(payload)
            break

        default:
            console.log('Unknown event:', eventType)
    }
}

function handleReactiveEvent(payload) {
    if (!payload || !payload.event) return
    switch (payload.event) {
        case 'browser_color_change':
            // ThemeTool 触发深色/亮色切换
            if (payload.mode === 'dark') {
                store.commit('setIsbackGround', true)
            } else if (payload.mode === 'light') {
                store.commit('setIsbackGround', false)
            }
            break
        case 'change_speaker':
            // TTS 音色切换
            if (payload.speaker) {
                store.commit('setSpeaker', payload.speaker)
            }
            break
        default:
            console.log('Unknown reactive event:', payload.event)
    }
}

function handleBinaryFrame(buffer) {
    if (buffer.byteLength < 2) return
    const view = new DataView(buffer)
    const frameType = view.getUint8(0)
    const audioData = new Int16Array(buffer.slice(1))

    if (frameType === 0x02) {
        playAudioChunk(audioData)
    }
}

// ============================================================
//  Commands
// ============================================================

function sendCommand(command, payload = {}) {
    if (!ws || ws.readyState !== WebSocket.OPEN) return
    ws.send(JSON.stringify({ command, seq: nextSeq(), payload }))
}

export function sendInterrupt() {
    sendCommand('voice.interrupt')
}

export function sendMute() {
    sendCommand('voice.mute')
}

export function sendUnmute() {
    sendCommand('voice.unmute')
}

export function endVoiceChat() {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({
            command: 'voice.session.leave',
            seq: nextSeq(),
            payload: {}
        }))
    }
    // delay close to let the leave message send
    setTimeout(() => cleanup(), 200)
}

// ============================================================
//  Cleanup
// ============================================================

function cleanup() {
    if (audioIntervals) {
        clearInterval(audioIntervals)
        audioIntervals = null
    }

    if (scriptProcessor) {
        scriptProcessor.disconnect()
        scriptProcessor = null
    }

    if (audioTrack) {
        audioTrack.stop()
        audioTrack = null
    }

    if (mediaStream) {
        mediaStream.getTracks().forEach(t => t.stop())
        mediaStream = null
    }

    if (audioContext && audioContext.state !== 'closed') {
        audioContext.close().catch(() => {})
        audioContext = null
    }

    if (ws) {
        ws.close()
        ws = null
    }

    isMicMuted = true
    ttsBuffer = []
    seq = 0

    store.commit('setIsCallActive', false)
    store.commit('setIsHaveVoice', false)
    store.commit('setPipelineState', 'IDLE')
    store.commit('setTtsPlaying', false)
    store.commit('setIsAgentActive', false)
    store.commit('clearAgentSteps')
    stopCallTimer()
}

export function getVoiceWebSocket() {
    return ws
}
