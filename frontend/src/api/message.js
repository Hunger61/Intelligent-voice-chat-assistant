import { getAuthToken } from './auth.js';

class MessageService {
  /**
   * 批量添加聊天历史记录
   * @param {string} assistantId - 助手ID
   * @param {Array<Array<string>>} messages - 消息数组，每条为 [role, content]
   */
  static async addMessages(assistantId, messages) {
    try {
      const token = getAuthToken();
      const response = await fetch(`/api/aiAssistant/${assistantId}/conversation`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Token: token } : {})
        },
        body: JSON.stringify({ messages })
      });
      return this._handleResponse(response);
    } catch (error) {
      console.error('Error adding messages:', error);
      throw error;
    }
  }

  /**
   * 分页查询聊天记录（按ID降序，最新在前）
   * @param {string} assistantId - 助手ID
   * @param {number} size - 每页大小
   * @param {number} pageNum - 页码（从1开始）
   */
  static async findMessagesByPage(assistantId, size, pageNum) {
    try {
      const token = getAuthToken();
      const response = await fetch(`/api/aiAssistant/${assistantId}/conversation-log`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Token: token } : {})
        }
      });
      const result = await this._handleResponse(response);
      const rows = Array.isArray(result?.data) ? result.data : [];
      return rows.map(([role, content]) => ({
        role: this._normalizeRole(role),
        content
      }));
    } catch (error) {
      console.error('Error fetching messages:', error);
      throw error;
    }
  }

  /**
   * 重置助手历史对话（清空对话，生成新对话ID）
   * @param {string} assistantId - 要重置的助手ID
   */
  static async resetHistory(assistantId) {
    try {
      const token = getAuthToken();
      const response = await fetch(`/api/aiAssistant/${assistantId}/conversation-log`, {
        method: 'DELETE',
        headers: {
          ...(token ? { Token: token } : {})
        }
      });
      return this._handleResponse(response);
    } catch (error) {
      console.error('Error resetting history:', error);
      throw error;
    }
  }

  static async _handleResponse(response) {
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `HTTP error! status: ${response.status}`);
    }

    const text = await response.text();
    if (!text) return { success: true, data: null };

    try {
      const result = JSON.parse(text);
      if (Array.isArray(result)) {
        return { success: true, data: result };
      }
      if (result && typeof result === 'object' && Object.prototype.hasOwnProperty.call(result, 'success')) {
        if (result.success === false) {
          throw new Error(result.message || 'Request failed');
        }
        return result;
      }
      return { success: true, data: result };
    } catch {
      return { success: true, data: text };
    }
  }

  static _normalizeRole(role) {
    if (!role) return 'assistant';
    const normalized = String(role).toLowerCase();
    if (normalized === 'user') return 'user';
    if (normalized === 'assistant') return 'assistant';
    return normalized;
  }
}

export default MessageService;
