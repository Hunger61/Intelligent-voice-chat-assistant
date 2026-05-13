# VocalChat WebSocket 协议设计

> 更新时间：2026-05-13
> 状态：设计阶段，待实现

---

## 目录

- [1. 概述](#1-概述)
- [2. 连接](#2-连接)
- [3. 消息格式](#3-消息格式)
- [4. Server → Client 事件](#4-server--client-事件)
- [5. Client → Server 命令](#5-client--server-命令)
- [6. Binary 帧 — 音频数据传输](#6-binary-帧--音频数据传输)
- [7. 流水线状态机](#7-流水线状态机)
- [8. 典型交互流程](#8-典型交互流程)
- [9. 错误码](#9-错误码)
- [10. 与现有代码的对应关系](#10-与现有代码的对应关系)

---

## 1. 概述

语音聊天场景需要一个**双向、低延迟、支持流式传输**的通信通道。WebSocket 承载三类数据：

| 类别 | 方向 | 帧类型 | 说明 |
|------|------|--------|------|
| 命令 | Client → Server | Text (JSON) | 会话控制、配置、打断等 |
| 事件 | Server → Client | Text (JSON) | 状态推送、ASR 结果、LLM token、TTS 状态 |
| 音频 | 双向 | Binary | PCM 麦克风采集 / Opus TTS 合成 |

> **与纯文本聊天的关系**：纯文本对话已统一走 SSE（`POST /api/aiAssistant/streamGenerateReply`）。本协议专为**语音交互**设计，复用同一个 `/ws/chat` 端点，在现有 `Command` / `Event` 体系上扩展。

---

## 2. 连接

```
ws://<host>:<port>/ws/chat?token=<jwt_token>
```

- 握手阶段由 `FrontEndWebSocketInterceptor` 校验 JWT，失败返回 401
- 连接建立后注册到 `WebSocketSessionManager`（UserId ↔ Session）
- 心跳：复用 WebSocket 标准 Ping/Pong 帧，间隔 15s
- 超时：30 分钟无活动断开
- 断线重连：客户端指数退避重试，重连后通过 `seq` 恢复状态

---

## 3. 消息格式

### 3.1 通用信封

所有 Text 帧 JSON 遵循统一结构：

```json
{
  "command": "<command_name>",
  "seq": 123,
  "payload": { ... }
}
```

```json
{
  "event": "<event_name>",
  "seq": 456,
  "payload": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `command` | string | 客户端→服务端，多态路由 discriminator |
| `event` | string | 服务端→客户端，多态路由 discriminator |
| `seq` | number | 单调递增消息序号，用于断线重连去重和排序 |
| `payload` | object | 具体事件/命令的业务数据 |

### 3.2 多态路由

沿用现有 Jackson `@JsonTypeInfo` + `@JsonSubTypes` 模式：

- **Command 抽象类**：`property = "command"`，`@JsonSubTypes` 注册所有子类
- **Event 抽象类**（新增）：`property = "event"`，`@JsonSubTypes` 注册所有子类

> 现有问题：`ASREvent` 使用 `"type"` 字段、`AnswerEvent` 使用 `"event"` 字段，缺少统一基类。本次设计统一为 `"event"` discriminator，所有出站事件继承 `Event` 抽象类。

---

## 4. Server → Client 事件

### 4.1 语音会话生命周期

#### `voice.session.started`

语音通道建立成功，可以开始交互。

```json
{
  "event": "voice.session.started",
  "seq": 1,
  "payload": {
    "sessionId": "uuid"
  }
}
```

#### `voice.session.ended`

语音通道关闭。

```json
{
  "event": "voice.session.ended",
  "seq": 99,
  "payload": {
    "sessionId": "uuid",
    "reason": "user_left | timeout | error"
  }
}
```

---

### 4.2 ASR 识别（语音 → 文本）

#### `voice.asr.interim`

流式中间识别结果，会持续推送直到 `voice.asr.final`。

```json
{
  "event": "voice.asr.interim",
  "seq": 10,
  "payload": {
    "text": "今天天气",
    "timestamp": 1715587200000,
    "isFinal": false
  }
}
```

#### `voice.asr.final`

最终识别结果。此时流水线从 LISTENING 转入 THINKING。

```json
{
  "event": "voice.asr.final",
  "seq": 11,
  "payload": {
    "text": "今天天气怎么样",
    "timestamp": 1715587200500,
    "duration": 2800
  }
}
```

---

### 4.3 LLM 回复（文本生成，流式）

#### `voice.llm.token`

逐 token 推送生成的文本。

```json
{
  "event": "voice.llm.token",
  "seq": 20,
  "payload": {
    "token": "今天",
    "index": 0
  }
}
```

#### `voice.llm.thinking`

深度思考模式的思维链 token（仅启用深度思考时推送）。

```json
{
  "event": "voice.llm.thinking",
  "seq": 21,
  "payload": {
    "token": "用户询问天气，需要查询...",
    "index": 5
  }
}
```

#### `voice.llm.complete`

LLM 回复完成。此时流水线从 THINKING 转入 SPEAKING。

```json
{
  "event": "voice.llm.complete",
  "seq": 50,
  "payload": {
    "fullText": "今天天气晴朗，气温 20-28 度...",
    "chatId": "uuid",
    "dialogueId": "uuid"
  }
}
```

---

### 4.4 TTS 播放（文本 → 语音）

#### `voice.tts.started`

开始播放语音回复。

```json
{
  "event": "voice.tts.started",
  "seq": 51,
  "payload": {
    "chatId": "uuid"
  }
}
```

#### `voice.tts.ended`

语音回复播放完毕。

```json
{
  "event": "voice.tts.ended",
  "seq": 52,
  "payload": {
    "chatId": "uuid"
  }
}
```

#### `voice.tts.interrupted`

播放被用户打断。

```json
{
  "event": "voice.tts.interrupted",
  "seq": 53,
  "payload": {
    "chatId": "uuid",
    "reason": "user_interrupt"
  }
}
```

---

### 4.5 流水线状态

#### `voice.pipeline.state`

语义化状态变更。前端驱动 UI 动画的核心事件。

```json
{
  "event": "voice.pipeline.state",
  "seq": 5,
  "payload": {
    "state": "LISTENING"
  }
}
```

| state | 含义 | UI 表现 |
|-------|------|---------|
| `IDLE` | 空闲 | 无动画 |
| `LISTENING` | 正在收音 | 麦克风波形 / 音量指示 |
| `THINKING` | AI 思考中 | 思考动画 / token 滚动 |
| `SPEAKING` | 播放回复 | 头像动画 / 字幕滚动 |

---

### 4.6 说话人状态

#### `voice.speaker.changed`

频道内其他用户开始/停止说话（多人场景预留）。

```json
{
  "event": "voice.speaker.changed",
  "seq": 6,
  "payload": {
    "userId": "uuid",
    "isSpeaking": true
  }
}
```

---

### 4.7 错误

#### `voice.error`

```json
{
  "event": "voice.error",
  "seq": 90,
  "payload": {
    "code": "ASR_TIMEOUT",
    "message": "语音识别超时，请重试",
    "recoverable": true
  }
}
```

---

## 5. Client → Server 命令

### 5.1 语音会话

#### `voice.session.join`

加入语音通道，同时携带 AI 助手和 TTS 配置。

```json
{
  "command": "voice.session.join",
  "seq": 1,
  "payload": {
    "aiAssistantId": "uuid",
    "tts": {
      "speaker": "female_1",
      "speed": 1.0,
      "volume": 80
    }
  }
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `aiAssistantId` | string | 是 | AI 助手 UUID |
| `tts.speaker` | string | 否 | TTS 音色 |
| `tts.speed` | number | 否 | 语速，默认 1.0 |
| `tts.volume` | number | 否 | 音量 0-100，默认 80 |

#### `voice.session.leave`

```json
{ "command": "voice.session.leave", "seq": 100, "payload": {} }
```

### 5.2 控制

打断当前 TTS 播放 / LLM 生成：

```json
{ "command": "voice.interrupt", "seq": 60, "payload": {} }
```

静音 / 取消静音麦克风：

```json
{ "command": "voice.mute", "seq": 61, "payload": {} }
```

```json
{ "command": "voice.unmute", "seq": 62, "payload": {} }
```

### 5.3 完整 Command 注册表

| command | 说明 | payload |
|---------|------|---------|
| `voice.session.join` | 加入语音通道 | `{ aiAssistantId, tts? }` |
| `voice.session.leave` | 离开语音通道 | `{}` |
| `voice.interrupt` | 打断当前操作 | `{}` |
| `voice.mute` | 静音麦克风 | `{}` |
| `voice.unmute` | 取消静音 | `{}` |

---

## 6. Binary 帧 — 音频数据传输

### 6.1 帧格式

每个 Binary 帧前 1 字节为类型标记，后续为音频数据：

| 偏移 | 大小 | 说明 |
|------|------|------|
| 0 | 1 byte | 类型：`0x01` = 上行音频（mic），`0x02` = 下行音频（TTS） |
| 1 | N bytes | 音频载荷 |

### 6.2 音频参数

| 参数 | 上行（Mic → Server） | 下行（Server → Client） |
|------|---------------------|------------------------|
| 编码 | PCM | Opus |
| 采样率 | 16000 Hz | 24000 Hz |
| 位深 | 16 bit | — |
| 声道 | mono | mono |
| 帧时长 | 20ms（640 bytes/frame） | 20ms |
| 码率 | ~256 kbps | ~24 kbps |

### 6.3 上行音频流控

- 客户端在 `voice.session.started` 后开始发送 Binary 帧
- 客户端在 `voice.pipeline.state` 变更时可能暂停发送（如进入 SPEAKING 状态时）
- 服务端检测 VAD（Voice Activity Detection）静音段后自动结束收音

---

## 7. 流水线状态机

```
                    voice.session.join
                           │
                    ┌──────▼──────┐
                    │    IDLE     │◄────────────────────────────┐
                    └──────┬──────┘                             │
                           │                                    │
                    VAD 检测到语音                               │
                           │                                    │
                    ┌──────▼──────┐                             │
              ┌────►│  LISTENING  │                             │
              │     └──────┬──────┘                             │
              │            │                                    │
              │     VAD 检测静默                                 │
              │            │                                    │
              │     ┌──────▼──────┐    LLM 流式完成              │
              │     │  THINKING   │────────────┐                │
              │     └──────┬──────┘            │                │
              │            │                   │                │
              │     TTS 开始播放          ┌────▼───────┐        │
              │            │              │  SPEAKING  │        │
              │            └──────────────►            ├────────┘
              │                           └────┬───────┘
              │                                │
              │                    用户打断 / 播放完成
              │                                │
              └────────────────────────────────┘
```

状态转移路径：

| 当前状态 | 触发事件 | 下一状态 |
|----------|---------|---------|
| `IDLE` | VAD 检测到语音 | `LISTENING` |
| `LISTENING` | VAD 检测到静默 | `THINKING` |
| `LISTENING` | 用户手动打断 | `IDLE` |
| `THINKING` | LLM 流式完成 | `SPEAKING` |
| `THINKING` | 用户打断 | `IDLE` |
| `SPEAKING` | 播放完成 | `IDLE` |
| `SPEAKING` | 用户打断 | `IDLE` |

任何状态收到 `voice.interrupt` 都回到 `IDLE`。

---

## 8. 典型交互流程

```
Client                           Server                        External Speech
  │                                 │                                 │
  │-- voice.session.join ---------─►│                                 │
  │   { aiAssistantId, tts }        │                                 │
  │◄── voice.session.started ──────│                                 │
  │◄── voice.pipeline.state: IDLE ─│                                 │
  │                                 │                                 │
  │                                 │── InviteCommand ───────────────►│  WebRTC 握手
  │                                 │◄── AnswerEvent (SDP) ───────────│
  │                                 │                                 │
  │-- Binary [PCM audio] ─────────►│── forward ─────────────────────►│  ASR 引擎
  │◄── voice.pipeline: LISTENING ──│                                 │
  │◄── voice.asr.interim ─────────│◄── asrDelta ────────────────────│  "今天"
  │◄── voice.asr.interim ─────────│◄── asrDelta ────────────────────│  "今天天气"
  │                                 │                                 │
  │                                 │         (VAD 静默检测)           │
  │◄── voice.asr.final ───────────│◄── asrFinal ────────────────────│  "今天天气怎么样"
  │◄── voice.pipeline: THINKING ──│                                 │
  │                                 │                                 │
  │                                 │── LLM streaming ────────────────│
  │◄── voice.llm.token ───────────│◄── streaming tokens ────────────│
  │◄── voice.llm.thinking ────────│  (deep thinking)                 │
  │◄── voice.llm.complete ────────│                                 │
  │                                 │                                 │
  │◄── voice.pipeline: SPEAKING ──│                                 │
  │◄── voice.tts.started ─────────│── TtsCommand ───────────────────►│  TTS 引擎
  │◄── Binary [Opus audio] ───────│◄── TTS audio ───────────────────│
  │◄── Binary [Opus audio] ───────│◄── TTS audio ───────────────────│
  │◄── voice.tts.ended ───────────│                                 │
  │◄── voice.pipeline: IDLE ──────│                                 │
  │                                 │                                 │
  │  ── 第二轮对话开始 ──                                              │
  │-- Binary [PCM audio] ─────────►│                                 │
  │                                 │                                 │
  │  ── 用户打断 ──                                                   │
  │-- voice.interrupt ────────────►│                                 │
  │◄── voice.tts.interrupted ─────│── InterruptCommand ─────────────►│
  │◄── voice.pipeline: IDLE ──────│                                 │
```

---

## 9. 错误码

| code | 说明 | 可恢复 |
|------|------|--------|
| `ASR_TIMEOUT` | 语音识别超时 | 是 |
| `ASR_NO_SPEECH` | 未检测到语音 | 是 |
| `LLM_TIMEOUT` | LLM 响应超时 | 是 |
| `LLM_ERROR` | LLM 调用失败 | 是 |
| `TTS_ERROR` | TTS 合成失败 | 是 |
| `SESSION_NOT_FOUND` | 语音会话不存在 | 否 |
| `AI_ASSISTANT_NOT_FOUND` | AI 助手未选择 | 是 |
| `AUDIO_CODEC_MISMATCH` | 音频编码不支持 | 否 |
| `RATE_LIMIT` | 请求过快 | 是 |
| `INTERNAL_ERROR` | 服务内部错误 | 否 |

---

## 10. 与现有代码的对应关系

### 10.1 现有类改动

| 现有文件 | 改动 |
|----------|------|
| `api/websocket/handler/FrontEndWebSocketHandler.java` | 补全 `handleGenerateCommand` 中的 LLM 调用；新增 Binary 帧处理分支；补全 `afterConnectionClosed` 清理逻辑 |
| `api/websocket/command/Command.java` | `@JsonSubTypes` 注册新增的 Voice*Command |
| `api/websocket/event/DomainEventListener.java` | 改为监听流水线事件，发送流式 `voice.llm.token` 等事件 |
| `infrastructure/websocket/WebSocketMessageSender.java` | 使用注入的 `ObjectMapper`，支持发送到所有会话 (broadcast) |
| `infrastructure/websocket/WebSocketSessionManager.java` | 补全 `afterConnectionClosed` 时清理映射 |

### 10.2 新增类

| 新增文件 | 说明 |
|----------|------|
| `api/websocket/event/Event.java` | 出站事件抽象基类，`@JsonTypeInfo(property = "event")` |
| `api/websocket/event/VoiceEvent.java` | 语音相关事件基类 |
| `api/websocket/event/VoiceAsrInterimEvent.java` | `voice.asr.interim` |
| `api/websocket/event/VoiceAsrFinalEvent.java` | `voice.asr.final` |
| `api/websocket/event/VoiceLlmTokenEvent.java` | `voice.llm.token` |
| `api/websocket/event/VoiceLlmThinkingEvent.java` | `voice.llm.thinking` |
| `api/websocket/event/VoiceLlmCompleteEvent.java` | `voice.llm.complete` |
| `api/websocket/event/VoiceTtsStartedEvent.java` | `voice.tts.started` |
| `api/websocket/event/VoiceTtsEndedEvent.java` | `voice.tts.ended` |
| `api/websocket/event/VoiceTtsInterruptedEvent.java` | `voice.tts.interrupted` |
| `api/websocket/event/VoicePipelineStateEvent.java` | `voice.pipeline.state` |
| `api/websocket/event/VoiceErrorEvent.java` | `voice.error` |
| `api/websocket/command/VoiceSessionJoinCommand.java` | `voice.session.join`（payload 含 `aiAssistantId` + 可选 `tts`） |
| `api/websocket/command/VoiceSessionLeaveCommand.java` | `voice.session.leave` |
| `api/websocket/command/VoiceInterruptCommand.java` | `voice.interrupt` |
| `api/websocket/command/VoiceMuteCommand.java` | `voice.mute` |
| `api/websocket/command/VoiceUnmuteCommand.java` | `voice.unmute` |
| `domain/model/speech/PipelineState.java` | 流水线状态枚举：IDLE / LISTENING / THINKING / SPEAKING |
| `application/service/VoicePipelineOrchestrator.java` | 语音流水线编排：协调 ASR → LLM → TTS，管理状态转移 |
| `infrastructure/audio/AudioFrameCodec.java` | Binary 帧编解码工具 |

### 10.3 废弃项

| 现有类 | 处理 |
|--------|------|
| `api/websocket/event/ASREvent.java` | 替换为 `VoiceAsrInterimEvent` / `VoiceAsrFinalEvent` |
| `api/websocket/event/AnswerEvent.java` | 替换为 `VoiceLlmTokenEvent` / `VoiceLlmCompleteEvent` |
| `api/websocket/event/ErrorEvent.java` | 替换为 `VoiceErrorEvent` |
| `api/websocket/command/GenerateCommand.java` | 语音模式下不再需要（文本输入走 SSE） |
