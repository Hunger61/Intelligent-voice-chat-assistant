import { getAuthToken } from './auth.js';

class KnowledgeService {
  /**
   * 创建知识库
   * @param {Object} data - { name, description }
   */
  static async createKnowledgeBase(data) {
    const token = getAuthToken();
    const response = await fetch('/api/knowledge-base', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      },
      body: JSON.stringify({
        name: data.name,
        description: data.description || ''
      })
    });
    return this._handleResponse(response);
  }

  /**
   * 获取当前用户的知识库列表
   */
  static async getKnowledgeBasesByUserId() {
    const token = getAuthToken();
    const response = await fetch('/api/knowledge-base', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      }
    });
    return this._handleResponse(response);
  }

  /**
   * 删除知识库
   * @param {string} id - 知识库ID
   */
  static async deleteKnowledgeBase(id) {
    const token = getAuthToken();
    const response = await fetch(`/api/knowledge-base/${id}`, {
      method: 'DELETE',
      headers: {
        ...(token ? { Token: token } : {})
      }
    });
    return this._handleResponse(response);
  }

  /**
   * 获取知识库内文件列表（后端待实现）
   * @param {string} knowledgeBaseId
   */
  static async getKnowledgeBaseFiles(knowledgeBaseId) {
    return this._unsupported();
  }

  /**
   * 上传文件到知识库（后端待实现）
   */
  static async uploadFile() { return this._unsupported(); }

  /**
   * 获取文件解析状态（后端待实现）
   */
  static async getFileStatus() { return this._unsupported(); }

  /**
   * 删除知识库内文件（后端待实现）
   */
  static async deleteFile() { return this._unsupported(); }

  /**
   * 批量添加文件到知识库（后端待实现）
   */
  static async addFilesToKnowledgeBase() { return this._unsupported(); }

  /**
   * 批量移除文件（后端待实现）
   */
  static async removeFilesFromKnowledgeBase() { return this._unsupported(); }

  /**
   * 查询文件处理任务状态（后端待实现）
   */
  static async getKnowledgeBaseJobStatus() { return this._unsupported(); }

  /**
   * @deprecated 使用 getKnowledgeBasesByUserId() 代替
   */
  static async getFilesByUserId() { return this._unsupported(); }

  static _unsupported() {
    throw new Error('知识库文件管理接口在当前后端版本未实现');
  }

  static async _handleResponse(response) {
    if (!response.ok) {
      const errorText = await response.text();
      let message = errorText;
      try {
        const errorData = JSON.parse(errorText);
        message = errorData.message || `HTTP error! status: ${response.status}`;
      } catch {
        message = errorText || `HTTP error! status: ${response.status}`;
      }
      throw new Error(message);
    }

    const text = await response.text();
    if (!text) return { success: true, data: null };

    try {
      const parsed = JSON.parse(text);
      if (parsed && typeof parsed === 'object' && Object.prototype.hasOwnProperty.call(parsed, 'success')) {
        if (parsed.success === false) {
          throw new Error(parsed.message || 'Request failed');
        }
        return parsed;
      }
      return { success: true, data: parsed };
    } catch (e) {
      if (e instanceof Error && e.message !== 'Unexpected end of JSON input') throw e;
      return { success: true, data: text };
    }
  }
}

export default KnowledgeService;
