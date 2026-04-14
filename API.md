# VocalChat API 文档（前后端分离，最佳实践版）

> 更新时间：2026-04-07  
> 依据代码：`backend/src/main/java/**`、`frontend/src/api/**`、`frontend/src/pages/**`

## 0. 变更标注（相对旧版文档）

- `【新增】` 助手列表返回字段包含 `id/name/description/character`（已对齐前端主流程）。
- `【修改】` 响应约定明确支持三种形态：`BaseResult`、裸 JSON、空响应（`204/void` 场景）。
- `【修改】` 会话日志角色规范化为小写 `user/assistant`（前端展示约定）。
- `【删除】` 旧地址 `ws://localhost:9090/ai_assistant/text_chat` 与 `ws://localhost:9091/ai_assistant/vocal_chat` 不再作为主协议，仅作为遗留代码说明。

## 1. 基础信息

- HTTP Base URL：`http://localhost:8080`
- WebSocket Base URL：`ws://localhost:8080/ws/chat?token=<JWT>`
- 鉴权：
  - HTTP：请求头 `Token: <JWT>`
  - WebSocket：握手 query `token=<JWT>`

## 2. 通用约定

### 2.1 返回体兼容策略（联调必读）

当前项目运行态可能出现以下三类返回：

1. `BaseResult<T>` 包装：

```json
{
  "success": true,
  "code": null,
  "message": null,
  "data": {}
}
```

2. 裸 JSON（数组/对象/字符串）
3. 空响应（`void`）

原因：`@AutoResult` 存在，但 `BaseResultHandler` 包扫描配置与当前包名不一致，是否生效依赖实际运行配置。

### 2.2 错误码（`ErrorEnum`）

| code | message |
|---|---|
| 1000 | 系统异常，请联系管理员 |
| 1001 | 验证码不正确 |
| 1002 | 邮箱已被使用 |
| 1003 | 邮箱未找到 |
| 1004 | 密码不正确 |
| 2001 | AI 助手 ID 为空 |
| 2002 | AI 助手不存在 |
| 3001 | 对话不存在 |

## 3. HTTP API

### 3.1 用户接口

| 状态 | 方法 | 路径 | 鉴权 | 前端使用 |
|---|---|---|---|---|
| 已实现 | POST | `/api/public/user/register` | 跳过 | `frontend/src/api/user.js` |
| 已实现 | POST | `/api/public/user/login` | 跳过 | `frontend/src/api/user.js` |
| 部分实现 | POST | `/api/public/user/getVerificationCode?email=...` | 跳过 | `frontend/src/api/user.js` |
| 已实现 | POST | `/api/public/user/logout` | 需要 | `frontend/src/api/user.js` |
| 部分实现 | GET | `/api/public/user/info` | 需要 | 当前前端未依赖 |

#### `POST /api/public/user/register`

请求：

```json
{
  "nickName": "fate",
  "email": "fate@example.com",
  "password": "123456",
  "verificationCode": "123456"
}
```

响应：JWT 字符串（可能在 `data`，也可能裸返回）。

#### `POST /api/public/user/login`

请求：

```json
{
  "email": "fate@example.com",
  "password": "123456"
}
```

响应：JWT 字符串（前端持久化键 `vocalchat_token`）。

#### `POST /api/public/user/getVerificationCode`

- 参数：`email`（query）
- 当前实现返回 `void`；前端已按“空响应成功”处理。

#### `POST /api/public/user/logout`

- Header：`Token`
- 当前实现返回 `void`。

#### `GET /api/public/user/info`

- 当前代码返回 `null`（占位实现）。

### 3.2 助手接口

| 状态 | 方法 | 路径 | 鉴权 | 前端使用 |
|---|---|---|---|---|
| 部分实现 | POST | `/api/public/aiAssistant/createNewAssistant` | 需要 | `frontend/src/api/assistant.js` |
| 已实现（弃用） | POST | `/api/public/aiAssistant/generateReply` | 跳过 | 前端未调用 |
| 已实现（推荐） | POST | `/api/public/aiAssistant/streamGenerateReply` | 跳过 | `frontend/src/api/assistant.js` |
| 已实现 | GET | `/api/public/aiAssistant/aiAssistants` | 需要 | `frontend/src/api/assistant.js` |
| 已实现 | GET | `/api/public/aiAssistant/{aiAssistantId}/conversation-log` | 需要 | `frontend/src/api/assistant.js`、`frontend/src/api/message.js` |

#### `POST /api/public/aiAssistant/createNewAssistant`

请求：

```json
{
  "name": "默认助手",
  "description": "用于测试",
  "character": "你是一个AI语音助手"
}
```

当前行为：`void` 返回（未返回新建助手 ID）。

#### `GET /api/public/aiAssistant/aiAssistants`

响应（`【新增】` 字段已在后端 VO 对齐）：

```json
[
  {
    "id": "assistant_id",
    "name": "默认语音助手",
    "description": "默认描述",
    "character": "你是一个AI语音助手"
  }
]
```

#### `GET /api/public/aiAssistant/{aiAssistantId}/conversation-log`

- 返回：`List<Pair<String,String>>`
- 实际数据形态：二维数组，如：

```json
[
  ["USER", "你好"],
  ["ASSISTANT", "你好，我在。"]
]
```

前端在 `MessageService` 中会规范化为小写角色：`user/assistant`。

#### `POST /api/public/aiAssistant/streamGenerateReply`（SSE）

请求：

```json
{
  "question": "你好",
  "aiAssistantId": "assistant_id"
}
```

事件：`started`、`token`、`fallback`、`done`、`error`、`heartbeat`。

## 4. WebSocket 协议（Java 后端）

### 4.1 入口

- `ws://localhost:8080/ws/chat?token=<JWT>`

### 4.2 命令

| command | payload | 状态 |
|---|---|---|
| `start_llm` | `ai_assistant_id` | 已实现 |
| `generate` | `content` | 未完成（handler TODO） |

### 4.3 现状风险

- 握手鉴权已实现。
- 命令分发已实现。
- `generate` 处理逻辑未闭环。
- `afterConnectionClosed` / `handleTransportError` 尚未实现。
- 握手属性键与会话注册读取键不一致，`userId` 绑定存在缺口。

## 5. 未实现能力（明确不扩展）

- 助手修改：`modify`
- 助手删除：`delete`
- 批量追加消息：`add_messages`
- 重置历史：`reset_history`
- 知识库接口全套（`frontend/src/api/knowledge.js`）

## 6. 前端落地约束（已执行）

- API 解析统一兼容：包装/裸值/空响应。
- 登录返回统一抽取 token 并写入 `localStorage`。
- 助手列表刷新统一取 `response.data || response`。
- 会话日志角色统一映射为 `user/assistant`，避免 UI 判断失败。

