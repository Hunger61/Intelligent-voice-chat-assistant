import { getAuthToken } from './auth.js';

class MessageService {
   /**
   * 批量添加聊天历史记录
   * @param {Array} messages - 消息数组
   * @returns {Promise<Array>} - 新增的消息记录
   */
  static async addMessages(messages) {
    throw new Error('该接口后端未实现：add_messages');
  }

  /**
   * 分页查询聊天记录（按ID降序，最新在前）
   * @param {number} assistantId - 助手ID
   * @param {number} size - 每页大小
   * @param {number} pageNum - 页码（从1开始）
   * @returns {Promise<Array>} - 分页消息数据
   */
  static async findMessagesByPage(assistantId, size, pageNum) {
    try {
      const token = getAuthToken();
      const response = await fetch(`/api/public/aiAssistant/${assistantId}/conversation-log`, {
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
   * 重置助手历史对话（会生成新ID）
   * @param {number} assistantId - 要重置的助手ID
   * @returns {Promise<Object>} - 新创建的助手数据
   */
  static async resetHistory(assistantId) {
    throw new Error('该接口后端未实现：reset_history');
  }

  /**
   * 处理响应
   * @param {Response} response 
   * @returns 
   */
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