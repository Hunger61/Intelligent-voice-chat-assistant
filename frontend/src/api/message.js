import { getAuthToken } from './auth.js';

class MessageService {
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
      return rows.map((item) => {
        // 兼容后端 Pair 序列化的 {left, right} 和数组 [role, content] 两种格式
        if (Array.isArray(item)) {
          return { role: this._normalizeRole(item[0]), content: item[1] };
        }
        if (item && typeof item === 'object') {
          return { role: this._normalizeRole(item.left ?? item.role), content: item.right ?? item.content };
        }
        return { role: 'assistant', content: String(item ?? '') };
      });
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
