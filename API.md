# VocalChat API 文档

> 更新时间：2026-05-10
> 依据代码：`backend/src/main/java/**`、`frontend/src/api/**`

---

## 目录

- [1. 概述](#1-概述)
- [2. 认证机制](#2-认证机制)
- [3. 通用响应格式](#3-通用响应格式)
- [4. 错误码](#4-错误码)
- [5. REST API](#5-rest-api)
  - [5.1 用户模块](#51-用户模块-api-public-user)
  - [5.2 AI 助手模块](#52-ai-助手模块-api-aiassistant)
  - [5.3 知识库模块](#53-知识库模块-api-knowledge-base)
  - [5.4 推荐新增接口](#54-推荐新增接口)
- [7. WebSocket 协议](#7-websocket-协议)
- [8. 前端对接现状](#8-前端对接现状)

---

## 1. 概述

| 项目 | 说明 |
|------|------|
| 基础 URL | `http://<host>:<port>`，无全局前缀 |
| 鉴权方式 | JWT，通过 HTTP Header `Token` 传递 |
| 请求格式 | `application/json`（文件上传除外） |
| 流式响应 | SSE（`text/event-stream`） |
| 实时通信 | WebSocket `ws://<host>:<port>/ws/chat?token=<JWT>` |

### 拦截器链

所有 HTTP 请求经过两层拦截器（`/**`）：

| 顺序 | 拦截器 | 职责 |
|------|--------|------|
| 0 | `RequestInterceptor` | 注入 `TRACE_ID` 到 MDC，记录请求耗时 |
| 1 | `UserInterceptor` | 验证 `Token` Header 中的 JWT，将 `User` 写入 `UserContext` |

- 方法标注 `@SkipToken` 时跳过鉴权。
- 鉴权失败返回 HTTP `401`，body 为 `"Unauthorized: Missing token"` 或 `"Unauthorized: User not found"`。

---

## 2. 认证机制

### Token 获取

注册或登录成功后，响应的 `data` 字段即为 JWT Token。

### Token 使用

所有需要鉴权的请求在 HTTP Header 中携带：

```
Token: <jwt_token_string>
```

### Token 生命周期

- 登录/注册时签发，Redis 中存储 key 为 `user:<userId>`，TTL 为 86400 秒（24 小时）。
- 登出时删除 Redis 中的 Token 记录。

### WebSocket 鉴权

握手阶段通过查询参数传递：

```
ws://<host>:<port>/ws/chat?token=<jwt_token_string>
```

鉴权失败则拒绝握手（HTTP 401）。

---

## 3. 通用响应格式

### BaseResult\<T\>

标注 `@AutoResult` 的方法，返回值自动包装：

```json
{
  "code": null,
  "message": null,
  "success": true,
  "data": "<实际载荷>"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | `Integer` | 成功为 `null`，失败为错误码 |
| `message` | `String` | 成功为 `null`，失败为错误描述 |
| `success` | `boolean` | `true` / `false` |
| `data` | `T` | 实际载荷，可为 `null` |

成功示例：

```json
{
  "code": null,
  "message": null,
  "success": true,
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

失败示例：

```json
{
  "code": 1004,
  "message": "密码不正确",
  "success": false,
  "data": null
}
```

### 兼容说明

部分接口未标注 `@AutoResult`（如助手管理类接口返回 `void`），此时 `BaseResultHandler` 不会包装响应。前端统一通过 `_handleResponse` 兼容三种形态：

1. `BaseResult` 包装（有 `success` 字段）
2. 裸 JSON（数组 / 对象 / 字符串）
3. 空响应（HTTP 200，body 为空）

---

## 4. 错误码

| 错误码 | 枚举常量 | 中文描述 |
|--------|----------|----------|
| 1000 | `COMMON_ERROR` | 系统异常，请联系管理员 |
| 1001 | `VERIFICATION_CODE_INCORRECT` | 验证码不正确 |
| 1002 | `EMAIL_ALREADY_USED` | 邮箱已被使用 |
| 1003 | `EMAIL_NOT_FOUND` | 邮箱未找到 |
| 1004 | `PASSWORD_INCORRECT` | 密码不正确 |
| 1005 | `NO_LOGIN` | 未登录或登录已过期 |
| 2001 | `AI_ASSISTANT_ID_NULL` | AI 助手 ID 为空 |
| 2002 | `AI_ASSISTANT_NOT_FOUND` | AI 助手不存在 |
| 3001 | `DIALOGUE_NOT_FOUND` | 对话不存在 |
| 4001 | `USER_INPUT_EMPTY` | 用户输入不能为空 |

---

## 5. REST API

### 5.1 用户模块 `/api/public/user`

#### `POST /register` — 注册

- **鉴权**：`@SkipToken`
- **响应**：`@AutoResult` → `BaseResult<String>`，data 为 JWT

请求体：

```json
{
  "nickName": "string // 昵称",
  "password": "string // 明文密码",
  "email": "string // 邮箱",
  "verificationCode": "string // 邮箱验证码"
}
```

成功响应：

```json
{
  "success": true,
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

可能错误码：`1001`、`1002`

---

#### `POST /login` — 登录

- **鉴权**：`@SkipToken`
- **响应**：`@AutoResult` → `BaseResult<String>`，data 为 JWT

请求体：

```json
{
  "email": "string // 邮箱",
  "password": "string // 明文密码"
}
```

成功响应：

```json
{
  "success": true,
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

可能错误码：`1003`、`1004`

---

#### `POST /getVerificationCode` — 发送验证码

- **鉴权**：`@SkipToken`
- **响应**：`@AutoResult` → `BaseResult<null>`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `email` | `String` | 是 | 接收验证码的邮箱 |

验证码为 6 位随机数字，Redis 缓存 5 分钟，异步邮件发送。

---

#### `POST /logout` — 登出

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<null>`

无请求参数，从 `UserContext` 获取当前用户，清除 Redis 中的 Token。

---

#### `GET /info` — 获取当前用户信息

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<UserInfoVO>`

成功响应：

```json
{
  "success": true,
  "data": {
    "id": "string // 用户 UUID",
    "nickName": "string // 昵称",
    "email": "string // 邮箱"
  }
}
```

---

### 5.2 AI 助手模块 `/api/aiAssistant`

> 注意：以下接口中，标注 `@AutoResult` 的自动包装为 `BaseResult`；未标注的返回原始 `void`。路径前缀为 `/api/aiAssistant`（非 `/api/public/aiAssistant`）。

#### `POST /createNewAssistant` — 创建助手

- **鉴权**：需要 Token
- **响应**：无包装（返回 `void`）

请求体：

```json
{
  "name": "string // 助手名称",
  "description": "string // 助手描述",
  "character": "string // 角色设定 / System Prompt",
  "knowledgeBaseId": "string // 关联知识库 ID（可选，空字符串表示不关联）"
}
```

创建助手时自动生成初始空对话（Dialogue）。

**注意**：当前返回 `void`，未返回新建助手的 ID。建议前端在调用后重新请求 `GET /aiAssistants` 获取最新列表。

---

#### `POST /modifyAssistantConfig` — 修改助手配置

- **鉴权**：需要 Token
- **响应**：无包装（返回 `void`）

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `aiAssistantId` | `String` | 是 | 助手 ID |

请求体：同 `/createNewAssistant`

---

#### `DELETE /deleteAssistant` — 删除助手

- **鉴权**：需要 Token
- **响应**：无包装（返回 `void`）

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `aiAssistantId` | `String` | 是 | 助手 ID |

---

#### `GET /aiAssistants` — 获取助手列表

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<List<AIAssistantVO>>`

返回当前用户创建的所有助手。

成功响应：

```json
{
  "success": true,
  "data": [
    {
      "id": "string // 助手 UUID",
      "name": "string // 助手名称",
      "description": "string // 描述",
      "character": "string // 角色设定",
      "knowledgeBaseId": "string // 关联知识库 ID（可为 null）"
    }
  ]
}
```

---

#### `GET /{aiAssistantId}/conversation-log` — 获取会话记录

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<List<Pair<String, String>>>`

路径参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `aiAssistantId` | `String` | 助手 ID |

成功响应：

```json
{
  "success": true,
  "data": [
    ["USER", "你好"],
    ["ASSISTANT", "你好！有什么可以帮你的？"],
    ["USER", "帮我写一首诗"],
    ["ASSISTANT", "好的，这是一首关于春天的诗..."]
  ]
}
```

`data` 为二维数组，每个元素 `[角色, 内容]`，按时间顺序排列。角色为 `USER` / `ASSISTANT`。前端会规范化为小写 `user` / `assistant`。

---

#### `POST /streamGenerateReply` — 流式生成回复（SSE）

- **鉴权**：需要 Token
- **Content-Type**：请求 `application/json`，响应 `text/event-stream`
- **响应**：`SseEmitter`（无 `@AutoResult` 包装）

请求体：

```json
{
  "question": "string // 用户问题",
  "aiAssistantId": "string // 助手 ID"
}
```

SSE 事件详见 [第 6 节](#6-sse-流式协议)。

---

#### `POST /{aiAssistantId}/conversation` — 追加会话消息

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<null>`

路径参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `aiAssistantId` | `String` | 助手 ID |

请求体：

```json
{
  "messages": [
    ["USER", "用户消息内容"],
    ["ASSISTANT", "助手回复内容"]
  ]
}
```

`messages` 为二维数组，每个元素 `[角色, 内容]`，角色取值 `USER` / `ASSISTANT`。

---

#### `DELETE /{aiAssistantId}/conversation-log` — 重置会话历史

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<null>`

路径参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `aiAssistantId` | `String` | 助手 ID |

清空当前对话并自动创建新的空对话，对用户感知为"开始新对话"。

---

### 5.3 知识库模块 `/api/knowledge-base`

#### `POST /api/knowledge-base` — 创建知识库

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<null>`

请求体：

```json
{
  "name": "string // 知识库名称",
  "description": "string // 知识库描述（可选，最长 200 字符）"
}
```

创建后自动设置状态为 `ACTIVE`，文档数和切片数初始为 0。

---

#### `GET /api/knowledge-base` — 获取知识库列表

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<List<KnowledgeBaseVO>>`

返回当前用户创建的所有知识库。

成功响应：

```json
{
  "success": true,
  "data": [
    {
      "id": "string // 知识库 UUID",
      "name": "string // 名称",
      "description": "string // 描述（可为 null）",
      "status": "ACTIVE",
      "documentCount": 5,
      "chunkCount": 128,
      "createdAt": "2026-05-10T12:00:00",
      "updatedAt": "2026-05-10T12:00:00"
    }
  ]
}
```

---

#### `GET /api/knowledge-base/{id}` — 获取知识库详情

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<KnowledgeBaseVO>`

路径参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `String` | 知识库 ID |

---

#### `PUT /api/knowledge-base/{id}` — 修改知识库

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<null>`

路径参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `String` | 知识库 ID |

请求体同 `POST /api/knowledge-base`。

---

#### `DELETE /api/knowledge-base/{id}` — 删除知识库

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<null>`

路径参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `String` | 知识库 ID |

---

#### 文件管理 `/api/knowledge-base/{id}/file`

##### `POST /{id}/file` — 上传文件

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<KnowledgeBaseFileVO>`
- **Content-Type**：`multipart/form-data`

表单字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `file` | `File` | 上传文件（支持 PDF / TXT / MD / DOCX 等） |

成功响应：

```json
{
  "success": true,
  "data": {
    "id": "string // 文件 UUID",
    "knowledgeBaseId": "string",
    "fileName": "doc.pdf",
    "fileType": "pdf",
    "fileSize": 102400,
    "status": "COMPLETED",
    "chunkCount": 0,
    "createdAt": "2026-05-10T12:00:00",
    "updatedAt": "2026-05-10T12:00:00"
  }
}
```

`status` 取值：`UPLOADING` → `UPLOADED` / `COMPLETED`（当 `ObjectStorageService` 不可用时回退为 `UPLOADED`，元数据正常入库）。

---

##### `GET /{id}/file` — 获取文件列表

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<List<KnowledgeBaseFileVO>>`

---

##### `DELETE /{id}/file/{fileId}` — 删除文件

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<null>`

---

##### `GET /{id}/file/{fileId}/status` — 查询文件状态

- **鉴权**：需要 Token
- **响应**：`@AutoResult` → `BaseResult<KnowledgeBaseFileVO>`

返回文件当前状态（`UPLOADING` / `UPLOADED` / `COMPLETED` / `FAILED`）及切片数量。

---

### 5.4 推荐新增接口

以下接口基于三个维度综合建议：

1. **前端 stub 分析**：`knowledge.js` 全部 11 个方法、`message.js` 中 2 个方法、`assistant.js` 中 2 个方法均抛出 "未实现" 错误
2. **领域模型储备**：`KnowledgeBase`、`SpeechProcessor`、`Session` 等聚合根已建模但无 API 暴露
3. **功能闭环需要**：用户自助管理（改密/改资料）、语音功能扩展等

---

#### 5.4.1 语音模块（建议 `/api/speech`）

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| `POST` | `/api/speech/asr` | 上传音频进行语音识别 | 中 |
| `POST` | `/api/speech/tts` | 文本转语音 | 中 |

ASR 请求：`multipart/form-data`，字段 `audio`（支持 wav/mp3/webm 等格式），附带 `aiAssistantId`。

TTS 请求体：

```json
{
  "text": "string // 要合成的文本",
  "voice": "string // 可选，语音风格"
}
```

TTS 响应：`audio/wav` 二进制流。

已有基础：`SpeechDomainService` 接口已定义（`TextToSpeech`、`registerSpeechProcessor`），`TextToSpeechApplicationService` 已监听 `QuestionAnsweredEvent` 自动触发生成。`AudioQuestionDTO` 已定义但未被任何 Controller 引用。

---

#### 5.4.2 助手管理扩展

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| `GET` | `/api/aiAssistant/{id}` | 获取单个助手详情 | **高** |
| `GET` | `/api/aiAssistant/{id}/conversation-log/page` | 分页获取会话记录 | 中 |

---

#### 5.4.3 用户自助管理（扩展 `/api/public/user`）

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| `PUT` | `/api/public/user/profile` | 修改昵称 | 中 |
| `PUT` | `/api/public/user/password` | 修改密码 | 中 |
| `DELETE` | `/api/public/user/account` | 注销账号 | 低 |

修改密码请求体：

```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

---

#### 5.4.4 系统

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| `GET` | `/api/health` | 健康检查（返回数据库/Redis/MinIO 连接状态） | 低 |

---

## 6. SSE 流式协议

### 端点

```
POST /api/aiAssistant/streamGenerateReply
Content-Type: application/json
Accept: text/event-stream
```

### 事件一览

| 事件名 | Data | 触发时机 |
|--------|------|----------|
| `started` | `"invoked"` | 请求已被接收，开始处理 |
| `heartbeat` | `"ping"` | 每 15 秒，保持连接活跃 |
| `token` | token 文本或 `""` | LLM 每输出一个 token |
| `fallback` | `"true"` | 主模型失败，切换到备用方案 |
| `done` | `"complete"` | 完整回复已推送并持久化 |
| `error` | 异常信息或 `"timeout"` | 异常发生（`"timeout"` = 30 分钟超时） |

### 典型事件流

```
event:started
data:invoked

event:token
data:你好

event:token
data:！

event:token
data:有什么

event:token
data:可以帮

event:token
data:你的？

event:done
data:complete
```

### 连接参数

| 参数 | 值 |
|------|------|
| 超时时间 | 30 分钟 |
| 心跳间隔 | 15 秒 |

---

## 7. WebSocket 协议

### 端点

```
ws://<host>:<port>/ws/chat?token=<jwt_token>
```

- 所有来源允许（Origin `*`）。
- 握手阶段鉴权，失败返回 401 并拒绝连接。
- 连接建立后 Session 注册到 `WebSocketSessionManager`（UserId ↔ Session 双向映射）。

### 客户端 → 服务端（Command）

消息为 JSON 文本，通过 `"command"` 字段多态路由：

#### `start_llm` — 选择助手

```json
{
  "command": "start_llm",
  "ai_assistant_id": "string // 助手 UUID"
}
```

将助手存入 WebSocket Session 属性（key: `"aiAssistant"`），供后续 `generate` 使用。

#### `generate` — 生成回复

```json
{
  "command": "generate",
  "content": "string // 用户输入"
}
```

> **状态**：`generate` 的实际 LLM 调用被注释（`FrontEndWebSocketHandler:102 //todo`），WebSocket 通道暂不可用于对话。当前请使用 REST `POST /streamGenerateReply`。

### 服务端 → 客户端（Event）

#### `chat_response` — 助手回复

由 `QuestionAnsweredEvent` 领域事件触发：

```json
{
  "event": "chat_response",
  "chat_id": "1",
  "payload": "助手回复的完整文本"
}
```

> 注意：`chat_id` 硬编码为 `"1"`（`//todo`）。

#### `repeat` — 回显用户输入

由 `QuestionReceivedEvent` 领域事件触发：

```json
{
  "type": "repeat",
  "payload": "识别出的用户问题文本"
}
```

### 已知问题

| 问题 | 位置 | 影响 |
|------|------|------|
| `afterConnectionClosed` 未实现 | `FrontEndWebSocketHandler:49` | Session 断连不清理，内存泄漏 |
| `handleTransportError` 未实现 | `FrontEndWebSocketHandler:45` | 传输异常无处理 |
| `generate` 的 LLM 调用被注释 | `FrontEndWebSocketHandler:102` | WebSocket 通道无法生成回复 |
| `chat_id` 硬编码 `"1"` | `DomainEventListener:26` | 无法区分不同对话 |
| Session 属性 key 不一致 | Interceptor: `userId.toString()` vs Handler: `"userId"` | 可能导致 Session 映射错误 |

---

## 8. 前端对接现状

### 已正常调用

| 前端文件 | 方法 | 后端接口 | 状态 |
|----------|------|----------|------|
| `user.js` | `register()` | `POST /api/public/user/register` | 正常 |
| `user.js` | `login()` | `POST /api/public/user/login` | 正常 |
| `user.js` | `getVerificationCode()` | `POST /api/public/user/getVerificationCode` | 正常 |
| `user.js` | `logout()` | `POST /api/public/user/logout` | 正常 |
| `assistant.js` | `add()` | `POST /api/aiAssistant/createNewAssistant` | 正常 |
| `assistant.js` | `findAll()` | `GET /api/aiAssistant/aiAssistants` | 正常 |
| `assistant.js` | `streamGenerateReply()` | `POST /api/aiAssistant/streamGenerateReply` | 正常 |
| `assistant.js` | `getConversationLog()` | `GET /api/aiAssistant/{id}/conversation-log` | 正常 |
| `assistant.js` | `modifyCommon()` | `POST /api/aiAssistant/modifyAssistantConfig` | 正常 |
| `assistant.js` | `delete()` | `DELETE /api/aiAssistant/deleteAssistant` | 正常 |
| `knowledge.js` | `createKnowledgeBase()` | `POST /api/knowledge-base` | 正常 |
| `knowledge.js` | `getKnowledgeBasesByUserId()` | `GET /api/knowledge-base` | 正常 |
| `knowledge.js` | `deleteKnowledgeBase()` | `DELETE /api/knowledge-base/{id}` | 正常 |
| `assistant.js` | `modifyCommon()` | `POST /api/aiAssistant/modifyAssistantConfig` | 正常 |
| `assistant.js` | `delete()` | `DELETE /api/aiAssistant/deleteAssistant` | 正常 |
| `knowledge.js` | `createKnowledgeBase()` | `POST /api/knowledge-base` | 正常 |
| `knowledge.js` | `getKnowledgeBasesByUserId()` | `GET /api/knowledge-base` | 正常 |
| `knowledge.js` | `deleteKnowledgeBase()` | `DELETE /api/knowledge-base/{id}` | 正常 |
| `knowledge.js` | `uploadFile()` | `POST /api/knowledge-base/{id}/file` | 正常 |
| `knowledge.js` | `getKnowledgeBaseFiles()` | `GET /api/knowledge-base/{id}/file` | 正常 |
| `knowledge.js` | `deleteFile()` | `DELETE /api/knowledge-base/{id}/file/{fileId}` | 正常 |
| `knowledge.js` | `getFileStatus()` | `GET /api/knowledge-base/{id}/file/{fileId}/status` | 正常 |
| `knowledge.js` | `addFilesToKnowledgeBase()` | `POST /api/knowledge-base/{id}/file`（批量） | 正常（复用 uploadFile） |
| `knowledge.js` | `removeFilesFromKnowledgeBase()` | `DELETE /api/knowledge-base/{id}/file/{fileId}`（批量） | 正常（复用 deleteFile） |
| `knowledge.js` | `getKnowledgeBaseJobStatus()` | `GET /api/knowledge-base/{id}/file/{fileId}/status` | 正常（复用 getFileStatus） |
| `message.js` | `findMessagesByPage()` | `GET /api/aiAssistant/{id}/conversation-log` | 正常（复用） |
| `message.js` | `addMessages()` | `POST /api/aiAssistant/{id}/conversation` | 正常 |
| `message.js` | `resetHistory()` | `DELETE /api/aiAssistant/{id}/conversation-log` | 正常 |
