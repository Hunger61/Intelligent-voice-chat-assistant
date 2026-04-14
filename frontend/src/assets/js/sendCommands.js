 import { getWebsocket } from "./websocket";
 import {
    showClickErrMessage,
    showErrMessage,
} from './showErrOrTrue.js';
import { getVcWebsocket } from "./webRTCAll.js";


// 发送启动大模型命令
export function sendStartLLMCommand(aiAssistantId) {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "start_llm",
      ai_assistant_id: aiAssistantId
    }));
    console.log("完成启动大模型命令发送");
  } else {
    console.error("WebSocket 未连接");
  }
}

// 发送生成回复命令（原sendMessage）
export function sendGenerateCommand(content) {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "generate_stream",
      content: content
    }));
    console.log("完成生成命令发送");
  } else {
     showClickErrMessage("WebSocket 未连接,请返回首页或重试")
    console.error("WebSocket 未连接");
  }
}

export function sendMessage(content){
  sendGenerateCommand(content);
}

// 发送中断生成命令
export function sendInterruptCommand() {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "interrupt"
    }));
    console.log("完成中断命令发送");
  } else {
    console.error("WebSocket 未连接");
  }
}

// 发送挂断命令
export function sendHangupCommand() {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "hangup"
    }));
    console.log("完成挂断命令发送");
  } else {
    console.error("WebSocket 未连接");
  }
}

// 发送重设人格命令
export function sendResetCharacterCommand(character) {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "reset_character",
      content: character,
    }));
    console.log("完成重设人格命令发送");
  } else {
    console.error("WebSocket 未连接");
  }
}

// 发送重置聊天历史命令
export function sendResetHistoryCommand() {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "reset_history"
    }));
    console.log("完成重置历史命令发送");
  } else {
    console.error("WebSocket 未连接");
  }
}

// 发送更改知识库命令
export function sendTcResetKnowledgeBaseCommand(knowledgeBaseId) {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "reset_knowledge_base",
      knowledge_base_id: knowledgeBaseId
    }));
    console.log("完成发送更改知识库命令");
  } else {
    console.error("WebSocket 未连接");
  }
}

// 发送开关知识库命令(纯文本大模型)
export function sendTcSwitchKnowledgeBaseCommand(status) {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "set_knowledge_base_status",
      status: status
    }));
    console.log("纯文本大模型完成发送开关知识库命令");
  } else {
    console.error("WebSocket 未连接");
  }
}
// 发送开关知识库命令(语音大模型)
export function sendVcSwitchKnowledgeBaseCommand(status) {
  let wsp = getVcWebsocket();
  if (wsp && wsp.readyState === WebSocket.OPEN) {
    wsp.send(JSON.stringify({
      command: "set_knowledge_base_status",
      status: status
    }));
    console.log("语音大模型完成发送开关知识库命令");
  } else {
    console.error("语音大模型 WebSocket 未连接");
  }
}

// 发送开关联网搜索Command
export function sendTcSetNetworkSearchStatusCommand(status){
  let ws = getWebsocket();
   if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "set_network_search_status",
      status: status
    }));
    console.log("纯文本大模型完成发送开关联网搜索命令");
  } else {
    console.error("WebSocket 未连接");
  }
}

export function sendTcSetDeepThinkingStatusCommand(status) {
  let ws = getWebsocket();
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      command: "set_deep_thinking_status", //固定值
      status: status    // true启用，false关闭
    }));
    console.log("纯文本大模型完成发送开关深度思考命令");
  } else {
    console.error("WebSocket 未连接");
  }
}