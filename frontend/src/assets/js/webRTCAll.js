import store from '../../store' // 导入 Vuex store
import { soundEffect } from './sound.js';
import { addNewObject } from './useMatter.js'; //小方块
import {startCallTimer, stopCallTimer} from './timer.js';
import { watch } from 'vue'
import {showSuccessMessage,showErrMessage} from './showErrOrTrue.js';
import { classifyWeather } from './weather.js'; //天气分类


let wsp;
let peerConnection = null;
let stream = null;
let audioTrack = null;
let isMicMuted = true;
let audioContext;
let noiseSuppressor;
let closeReason = '用户主动挂断';
let analysers;
let audioIntervals;
let audioDataCallback = null; // 用于传递音频数据的回调函数



// 切换麦克风状态
export function toggleMicrophone() {
    if (!audioTrack) {
        console.error('音频轨道未初始化');
        return;
    }

    isMicMuted = !isMicMuted;
    audioTrack.enabled = !isMicMuted;

    console.log(`麦克风${isMicMuted ? '关闭' : '开启'}`);
    return isMicMuted
}
// 设置音频数据回调
export function setAudioDataCallback(callback) {
    audioDataCallback = callback;
}


// 初始化音频处理（降噪、滤波等）
async function initAudioProcessing() {
    try {
        audioContext = new (window.AudioContext || window.webkitAudioContext)({
            sampleRate: 16000,
            latencyHint: 'interactive'
        });

        // 创建音频处理节点
        const source = audioContext.createMediaStreamSource(stream);
        const destination = audioContext.createMediaStreamDestination();

        // 噪声抑制（优先使用浏览器原生API，否则用高通滤波器）
        if (typeof audioContext.createNoiseSuppressor === 'function') {
            noiseSuppressor = audioContext.createNoiseSuppressor();
            source.connect(noiseSuppressor).connect(destination);
        } else {
            const highPassFilter = audioContext.createBiquadFilter();
            highPassFilter.type = 'highpass'; // 过滤低频噪声
            highPassFilter.frequency.value = 80;
            source.connect(highPassFilter).connect(destination);
        }

        // 创建本地音频分析器（用于监听本地音量）
        const localAnalyser = audioContext.createAnalyser();
        localAnalyser.fftSize = 4096;
        source.connect(localAnalyser);

        analysers = { local: localAnalyser, remote: null };
        audioTrack = destination.stream.getAudioTracks()[0];
        audioTrack.enabled = false; // 初始禁用麦克风

        startAudioMonitoring('local'); // 开始监听本地音频
        return destination.stream; // 返回处理后的音频流
    } catch (error) {
        console.error('音频处理初始化失败:', error);
        return stream; // 失败时返回原始流
    }
}

// 通用的音频监听函数，支持本地和远程流
function startAudioMonitoring(type) {
    const analyser = analysers[type];
    if (!analyser) return;

    const bufferLength = analyser.frequencyBinCount;
    const frequencyData = new Uint8Array(bufferLength);
    const timeDomainData = new Uint8Array(bufferLength);

    // 创建或复用定时器
    if (!audioIntervals) audioIntervals = {};

    audioIntervals[type] = setInterval(() => {
        // 检查是否需要关闭
        if (!store.getters.getIsCallActive) {
            stopAudioProcessing();
            return;
        }

        // 对本地流检查麦克风是否静音
        if (type === 'local' && (isMicMuted || !audioTrack?.enabled)) {
            // 当静音时，传递零值
            if (audioDataCallback && typeof audioDataCallback === 'function') {
                audioDataCallback({ type, volume: 0, pitch: 0 });
            }
            return;
        }

        // 获取音频数据
        analyser.getByteTimeDomainData(timeDomainData);
        analyser.getByteFrequencyData(frequencyData);

        // 计算音量 (RMS)
        let sum = 0;
        for (let i = 0; i < bufferLength; i++) {
            const value = (timeDomainData[i] - 128) / 128;
            sum += value * value;
        }
        const volume = Math.sqrt(sum / bufferLength);



        // 如果设置了回调，传递音频数据，包含类型标识
        if (audioDataCallback && typeof audioDataCallback === 'function') {
            audioDataCallback({
                type, volume,
                fftData: frequencyData,  // 新增FFT数据
                sampleRate: audioContext.sampleRate  // 新增采样率 
            });
        }
    }, 100); // 每300ms检测一次
}


// 停止音频处理并释放资源
function stopAudioProcessing() {
    // 清除所有定时器
    if (audioIntervals) {
        Object.values(audioIntervals).forEach(interval => {
            clearInterval(interval);
        });
        audioIntervals = null;
    }

    if (audioTrack) {
        audioTrack.stop();
        audioTrack = null;
    }

    // 关闭音频上下文
    if (audioContext && audioContext.state !== 'closed') {
        audioContext.close().then(() => {
            audioContext = null;
            analysers = null;
            console.log('音频处理已关闭');
        }).catch(err => {
            console.error('关闭音频上下文失败:', err);
        });
    }
}


// 在Vue组件中监听store变化
watch(() => store.getters.getIsCallActive, (isActive) => {
    if (!isActive) stopAudioProcessing();
});

// 启动WebRTC连接
export async function WebRTC() {
    try {
        wsp = new WebSocket('ws://localhost:9091/ai_assistant/vocal_chat');

        wsp.onerror = (error) => {
            console.error('WebSocket连接错误:', error);
            showErrMessage('WebSocket连接错误')
        };

        wsp.onclose = (event) => {
            console.log('WebSocket连接关闭:', event.reason);
        };

        wsp.onopen = async () => {
            console.log('WebSocket连接已建立');
            console.log(store.getters.getIsOnline)

            let llm_config = {
                assistant_id: Number.isInteger(store.getters.getSelectedId)
                    ? parseInt(store.getters.getSelectedId, 10)  // 确保是整数
                    : Math.floor(store.getters.getSelectedId),  // 若为浮点数，转为整数
                switch_knowledge_base: store.getters.getIsStartKnow,
                is_online: store.getters.getIsOnline,
            }
            let tts_config = {
                /*
                 * 爱小童: "601015" 男童声
                 * 爱小璟: "601012" 特色女声
                 * 智美: "101003" 客服女声
                 * 智瑜: "101001" 情感女声
                 * 智瑞: "101021"  新闻男声
                 * 智虹: "101022" 新闻女声
                 */
                speaker: store.getters.getSpeaker,

            }

            let config = {
                type: 'config',
                llm_config: llm_config,
                tts_config: tts_config,
            }
            wsp.send(JSON.stringify(config))

            await initializePeerConnection();
        };
        let totalTime = 0
        let isAdd = false
        let playId

        wsp.onmessage = async (event) => {
            try {
                const data = JSON.parse(event.data);

                if (data.type === 'answer') {
                    console.log('收到answer');
                    await handleAnswer(data.payload);
                }
                else if (data.type === 'repeat') {
                    let repeat = data.payload;
                    console.log(repeat);

                    store.commit('addnewMessagebyId', {
                        message: {
                            assistantId: store.getters.getSelectedId,// 获取当前选中的会话ID
                            role: 'user', // 指定消息类型
                            content: repeat
                        },
                        id: store.getters.getSelectedId // 获取当前选中的会话ID
                    });
                    store.commit('setIsHaveNewMessage', true)
                    isAdd = false
                }
                else if (data.type === 'response') {
                    let response = data.payload;

                    if (response.chat_id != -1) {
                        store.commit('setNewMessageContent', response);
                    }
                    console.log(response)
                    if (response.chat_id == -1) {
                        let newMessage = store.getters.getNewMessageContent;
                        if (newMessage != null && typeof newMessage.content === 'string' && newMessage.content.trim() !== '') {
                            store.commit('addnewMessagebyId', {
                                message: {
                                    play_id: newMessage.play_id,
                                    assistantId: store.getters.getSelectedId,
                                    role: 'assistant',
                                    content: newMessage.content
                                },
                                id: store.getters.getSelectedId
                            })
                            store.commit('setIsHaveNewMessage', false);
                            isAdd = true

                            store.commit('addDurationToMessageById', {
                                id: store.getters.getSelectedId,
                                playId: playId,
                                duration: totalTime
                            });
                        }
                        store.commit('clearNewMessageContent')
                    }

                }
                else if (data.type === 'hangup') {

                    closeWebRTC();
                    store.commit('setIsHaveNewMessage', false)

                    let newMessage = store.getters.getNewMessageContent;
                    if (newMessage && typeof newMessage.content === 'string' && newMessage.content.trim() !== '') {
                        store.commit('addnewMessagebyId', {
                            message: {
                                assistantId: store.getters.getSelectedId,
                                role: 'assistant',
                                content: newMessage.content
                            },
                            id: store.getters.getSelectedId
                        })
                    }

                    store.commit('clearNewMessageContent')

                    store.commit('addClosenewMessagebyId', store.getters.getSelectedId)
                }
                else if (data.type === 'tts_duration') {

                    playId = data.payload.play_id;

                    let duration = data.payload.duration;
                    if (isAdd) {
                        store.commit('addDurationToMessageById', {
                            id: store.getters.getSelectedId,
                            playId: playId,
                            duration: duration
                        });
                        totalTime = 0
                    } else {
                        console.log("xxxy")
                        totalTime = duration + totalTime
                    }
                } else if (data.type == "reactive_event") {
                    let payload = data.payload

                    let event = payload.event
                    let status = payload.status
                    let message = payload.message

                    if (event == "weather") {
                        let weatherArray = classifyWeather(message).categories
                        for (let i = 0; i < 10; i++) {
                            addNewObject(weatherArray, i)
                        }
                    } else if (event == "browser_color_change") {
                        console.log(message)
                        if (message == 'dark') {
                            store.commit('setIsbackGround', true)
                        } else {
                            store.commit('setIsbackGround', false)
                        }

                    } else if (event == "change_speaker") {
                        let speakerId = message

                    }


                }
                else {
                    console.log("未找到消息类型")
                }

            } catch (error) {
                console.error('消息处理错误:', error);
            }
        };
    } catch (error) {
        console.error('WebRTC初始化失败:', error);
        closeWebRTC();
    }
}


// 初始化WebRTC连接
async function initializePeerConnection() {
    try {
        // 获取用户音频流（请求麦克风权限，配置回声消除等参数）
        stream = await navigator.mediaDevices.getUserMedia({
            audio: {
                echoCancellation: { exact: true },
                googEchoCancellation: true,       // Chrome回声消除增强
                noiseSuppression: true,           // 噪声抑制
                autoGainControl: true,            // 自动增益
                sampleRate: 16000,                // 16kHz采样率
                channelCount: 1,                  // 单声道
                latency: 0.01,                    // 低延迟
                sampleSize: 16
            },
            video: false
        });

        // 音频预处理（降噪、滤波等）
        const processedStream = await initAudioProcessing();

        // 创建PeerConnection实例（配置STUN服务器用于NAT穿透）
        peerConnection = new RTCPeerConnection({
            iceServers: [{ urls: 'stun:stun.l.google.com:19302' }],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require'
        });

        // 监听连接状态变化
        peerConnection.onconnectionstatechange = () => {
            console.log('连接状态:', peerConnection.connectionState);
            if (peerConnection.connectionState === 'connected') {
                toggleMicrophone(); // 切换麦克风状态
                store.commit('setIsHaveVoice', true); // 更新语音连接状态
                startCallTimer(); // 开始通话计时
            }
            if (peerConnection.connectionState === 'failed') {
                closeWebRTC(); // 关闭连接
            }
        };

        // 添加处理后的音频轨道到连接
        peerConnection.addTrack(processedStream.getAudioTracks()[0], processedStream);

        // 处理接收到的远程音频流
        peerConnection.ontrack = (event) => {
            const hasAudio = event.streams[0].getAudioTracks().length > 0;
            if (hasAudio && audioContext) {
                const audioElement = new Audio();
                audioElement.srcObject = event.streams[0]; // 绑定远程流到音频元素

                // 音量控制（降低爆音）
                const gainNode = audioContext.createGain();
                gainNode.gain.value = 0.8;

                // 创建远程音频分析器（用于监听音量等）
                const remoteSource = audioContext.createMediaStreamSource(event.streams[0]);
                const remoteAnalyser = audioContext.createAnalyser();
                remoteAnalyser.fftSize = 4096;

                // 连接音频处理节点
                remoteSource.connect(remoteAnalyser);
                remoteSource.connect(gainNode).connect(audioContext.destination);

                analysers.remote = remoteAnalyser;
                startAudioMonitoring('remote'); // 开始监听远程音频
                audioElement.play().catch(e => console.error("远程音频播放失败:", e));
            }
        };

        // 创建并发送Offer（SDP协商）
        const offer = await peerConnection.createOffer({
            offerToReceiveAudio: true,
            offerToReceiveVideo: false
        });
        await peerConnection.setLocalDescription(offer);
        wsp.send(JSON.stringify({ type: 'offer', payload: offer.sdp })); // 通过WebSocket发送
        console.log('offer已发送')

    } catch (error) {
        console.error('PeerConnection初始化失败:', error);
        closeWebRTC();
    }
}
// 处理对方返回的Answer（完成SDP协商）
async function handleAnswer(answerSdp) {
    if (!peerConnection) return;
    try {
        await peerConnection.setRemoteDescription(
            new RTCSessionDescription({ type: 'answer', sdp: answerSdp })
        );
    } catch (error) {
        console.error("处理Answer失败:", error);
        closeWebRTC();
    }
}

// 获取PeerConnection
export function getPeerConnection() {
    if (!peerConnection) {
        throw new Error("PeerConnection未初始化");
    }
    return peerConnection;
}

// 关闭WebRTC连接
export function closeWebRTC() {
    try {


        console.log("前端发送hangup...")

        let closeMessage = {
            type: 'hangup',
            payload: closeReason
        }
        wsp.send(JSON.stringify(closeMessage))

        // 关闭音频处理
        if (audioContext && audioContext.state !== 'closed') {
            audioContext.close();
        }

        // 停止媒体流
        if (stream) {
            stream.getTracks().forEach(track => track.stop());
            stream = null;
            audioTrack = null;
        }

        // 关闭PeerConnection
        if (peerConnection) {
            peerConnection.close();
            peerConnection = null;
        }

        // 关闭WebSocket
        if (wsp) {
            wsp.close();
            wsp = null;
        }

        store.commit('setIsCallActive', false)
        store.commit('setIsHaveVoice', false)
        stopCallTimer()
        console.log("WebRTC连接已关闭");
        isMicMuted = true;
        soundEffect.play('close')
    } catch (error) {
        console.error('关闭连接时出错:', error);
    }
}
export function getVcWebsocket() {
    if (!wsp) {
        throw new Error("语音大模型 websocket 已失效")
    }
    return wsp;
}
