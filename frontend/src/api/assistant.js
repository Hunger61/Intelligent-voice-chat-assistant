import { getAuthToken, clearAuthToken } from './auth.js';

class AssistantService {
  /**
   * 添加新的语音助手
   * @param {Object} assistantData
   */
  static async add(assistantData) {
    try {
      const token = getAuthToken();
      const response = await fetch('/api/aiAssistant/createNewAssistant', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Token: token } : {})
        },
        body: JSON.stringify({
          name: assistantData.name,
          description: assistantData.description,
          character: assistantData.character || '你是一个AI语音助手',
          knowledgeBaseId: assistantData.knowledge_base_id || ''
        })
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const text = await response.text();
      if (!text) return { success: true };
      try {
        return JSON.parse(text);
      } catch {
        return { success: true, data: text };
      }
    } catch (error) {
      console.error('Error adding assistant:', error);
      throw error;
    }
  }

  /**
   * 获取所有语音助手
   */
  static async findAll() {
    try {
      const token = getAuthToken();
      const response = await fetch('/api/aiAssistant/aiAssistants', {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json',
                ...(token ? { Token: token } : {})
              }
            });

      return this._handleResponse(response);
    } catch (error) {
      console.error('Error fetching assistants:', error);
      throw error;
    }
  }



  /**
   * 修改助手名称、描述属性
   * @param {string} id - 助手ID
   * @param {string} name
   * @param {string} description
   * @param {string} character
   * @param {string} knowledge_base_id
   */
  static async modifyCommon(id, name, description, character, knowledge_base_id) {
    try {
      const token = getAuthToken();
      const params = new URLSearchParams({ aiAssistantId: id });
      const response = await fetch(`/api/aiAssistant/modifyAssistantConfig?${params.toString()}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Token: token } : {})
        },
        body: JSON.stringify({
          name,
          description,
          character: character || '',
          knowledgeBaseId: knowledge_base_id || ''
        })
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return { success: true };
    } catch (error) {
      console.error('Error modifying assistant:', error);
      throw error;
    }
  }

  /**
   * 删除助手
   * @param {string} id - 助手ID
   */
  static async delete(id) {
    try {
      const token = getAuthToken();
      const params = new URLSearchParams({ aiAssistantId: id });
      const response = await fetch(`/api/aiAssistant/deleteAssistant?${params.toString()}`, {
        method: 'DELETE',
        headers: {
          ...(token ? { Token: token } : {})
        }
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return { success: true };
    } catch (error) {
      console.error('Error deleting assistant:', error);
      throw error;
    }
  }

  static async getConversationLog(aiAssistantId) {
    const token = getAuthToken();
    const response = await fetch(`/api/aiAssistant/${aiAssistantId}/conversation-log`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      }
    });
    return this._handleResponse(response);
  }

  static async streamGenerateReply({ question, aiAssistantId, enableOnlineSearch, enableDeepThinking, signal, onStarted, onToken, onFallback, onThinking, onAgentThinking, onAgentToolCall, onAgentToolResult, onDone, onError }) {
    const token = getAuthToken();
    const response = await fetch('/api/aiAssistant/streamGenerateReply', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      },
      body: JSON.stringify({
        question,
        aiAssistantId,
        enableOnlineSearch: enableOnlineSearch || false,
        enableDeepThinking: enableDeepThinking || false
      }),
      signal
    });

    if (!response.ok || !response.body) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';

    const emitEvent = (chunk) => {
      const lines = chunk.split('\n');
      let eventName = 'message';
      const dataLines = [];
      for (const line of lines) {
        if (line.startsWith('event:')) {
          eventName = line.slice(6).trim();
        } else if (line.startsWith('data:')) {
          dataLines.push(line.slice(5).trim());
        }
      }
      const payload = dataLines.join('\n');
      if (eventName === 'started' && onStarted) onStarted(payload);
      else if (eventName === 'token' && onToken) onToken(payload);
      else if (eventName === 'thinking' && onThinking) onThinking(payload);
      else if (eventName === 'fallback' && onFallback) onFallback(payload);
      else if (eventName === 'agent_thinking' && onAgentThinking) onAgentThinking(payload);
      else if (eventName === 'agent_tool_call') {
        if (onAgentToolCall) {
          try { onAgentToolCall(JSON.parse(payload)); } catch { onAgentToolCall(payload); }
        }
      }
      else if (eventName === 'agent_tool_result') {
        if (onAgentToolResult) {
          try { onAgentToolResult(JSON.parse(payload)); } catch { onAgentToolResult(payload); }
        }
      }
      else if (eventName === 'done' && onDone) onDone(payload);
      else if (eventName === 'error' && onError) onError(payload);
    };

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      buffer += decoder.decode(value, { stream: true });
      const events = buffer.split('\n\n');
      buffer = events.pop() || '';
      for (const eventChunk of events) {
        if (eventChunk.trim()) emitEvent(eventChunk);
      }
    }
  }

  /**
   * Agent 流式执行 (SSE) — 与 streamGenerateReply 相同的事件模型 + Agent 事件
   */
  static async agentStream({ question, aiAssistantId, enableOnlineSearch, enableDeepThinking, signal, onStarted, onToken, onThinking, onAgentThinking, onAgentToolCall, onAgentToolResult, onDone, onError }) {
    const token = getAuthToken();
    const response = await fetch('/api/aiAssistant/agentStream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      },
      body: JSON.stringify({
        question,
        aiAssistantId,
        enableOnlineSearch: enableOnlineSearch || false,
        enableDeepThinking: enableDeepThinking || false
      }),
      signal
    });

    if (!response.ok || !response.body) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // 复用相同的 SSE 解析逻辑
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';

    const emitEvent = (chunk) => {
      const lines = chunk.split('\n');
      let eventName = 'message';
      const dataLines = [];
      for (const line of lines) {
        if (line.startsWith('event:')) {
          eventName = line.slice(6).trim();
        } else if (line.startsWith('data:')) {
          dataLines.push(line.slice(5).trim());
        }
      }
      const payload = dataLines.join('\n');
      if (eventName === 'started' && onStarted) onStarted(payload);
      else if (eventName === 'token' && onToken) onToken(payload);
      else if (eventName === 'thinking' && onThinking) onThinking(payload);
      else if (eventName === 'agent_thinking' && onAgentThinking) onAgentThinking(payload);
      else if (eventName === 'agent_tool_call') {
        if (onAgentToolCall) {
          try { onAgentToolCall(JSON.parse(payload)); } catch { onAgentToolCall(payload); }
        }
      }
      else if (eventName === 'agent_tool_result') {
        if (onAgentToolResult) {
          try { onAgentToolResult(JSON.parse(payload)); } catch { onAgentToolResult(payload); }
        }
      }
      else if (eventName === 'done' && onDone) onDone(payload);
      else if (eventName === 'error' && onError) onError(payload);
    };

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      buffer += decoder.decode(value, { stream: true });
      const events = buffer.split('\n\n');
      buffer = events.pop() || '';
      for (const eventChunk of events) {
        if (eventChunk.trim()) emitEvent(eventChunk);
      }
    }
  }

  /**
   * Agent 同步执行
   */
  static async agentRun({ question, aiAssistantId, enableOnlineSearch, enableDeepThinking }) {
    const token = getAuthToken();
    const response = await fetch('/api/aiAssistant/agentRun', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      },
      body: JSON.stringify({
        question,
        aiAssistantId,
        enableOnlineSearch: enableOnlineSearch || false,
        enableDeepThinking: enableDeepThinking || false
      })
    });
    return this._handleResponse(response);
  }

  /**
   * 获取可用工具列表
   */
  static async getTools() {
    const token = getAuthToken();
    const response = await fetch('/api/aiAssistant/tools', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      }
    });
    return this._handleResponse(response);
  }

  /**
   * 处理响应
   * @param {Response} response
   * @returns
   */
  static async _handleResponse(response) {
    if (!response.ok) {
      // 处理401未授权错误
      if (response.status === 401) {
        clearAuthToken();
        window.location.href = '/static/';
        throw new Error('登录已过期，请重新登录');
      }

      const errorText = await response.text();
      throw new Error(errorText || `HTTP error! status: ${response.status}`);
    }

    const text = await response.text();
    if (!text) return { success: true, data: null };

    try {
      const parsed = JSON.parse(text);
      if (Array.isArray(parsed)) return { success: true, data: parsed };
      if (parsed && typeof parsed === 'object' && Object.prototype.hasOwnProperty.call(parsed, 'success')) {
        if (parsed.success === false) {
          throw new Error(parsed.message || 'Request failed');
        }
        // 直接返回 parsed 对象，前端通过 response.data 获取实际数据
        return parsed;
      }
      return { success: true, data: parsed };
    } catch {
      return { success: true, data: text };
    }
  }

}

export default AssistantService;