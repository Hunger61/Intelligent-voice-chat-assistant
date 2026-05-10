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
   * 上传文件到知识库
   * @param {string} knowledgeBaseId - 知识库ID
   * @param {File} file - 文件对象
   */
  static async uploadFile(knowledgeBaseId, file) {
    const token = getAuthToken();
    const formData = new FormData();
    formData.append('file', file);
    const response = await fetch(`/api/knowledge-base/${knowledgeBaseId}/file`, {
      method: 'POST',
      headers: {
        ...(token ? { Token: token } : {})
      },
      body: formData
    });
    return this._handleResponse(response);
  }

  /**
   * 获取知识库内文件列表
   * @param {string} knowledgeBaseId
   */
  static async getKnowledgeBaseFiles(knowledgeBaseId) {
    const token = getAuthToken();
    const response = await fetch(`/api/knowledge-base/${knowledgeBaseId}/file`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      }
    });
    return this._handleResponse(response);
  }

  /**
   * 删除知识库内文件
   * @param {string} knowledgeBaseId
   * @param {string} fileId
   */
  static async deleteFile(knowledgeBaseId, fileId) {
    const token = getAuthToken();
    const response = await fetch(`/api/knowledge-base/${knowledgeBaseId}/file/${fileId}`, {
      method: 'DELETE',
      headers: {
        ...(token ? { Token: token } : {})
      }
    });
    return this._handleResponse(response);
  }

  /**
   * 查询文件处理状态
   * @param {string} knowledgeBaseId
   * @param {string} fileId
   */
  static async getFileStatus(knowledgeBaseId, fileId) {
    const token = getAuthToken();
    const response = await fetch(`/api/knowledge-base/${knowledgeBaseId}/file/${fileId}/status`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Token: token } : {})
      }
    });
    return this._handleResponse(response);
  }

  /**
   * 批量添加文件到知识库（合并到 uploadFile）
   * @param {string} knowledgeBaseId
   * @param {File[]} files
   */
  static async addFilesToKnowledgeBase(knowledgeBaseId, files) {
    const results = [];
    for (const file of files) {
      results.push(await this.uploadFile(knowledgeBaseId, file));
    }
    return { success: true, data: results };
  }

  /**
   * 批量移除文件
   * @param {string} knowledgeBaseId
   * @param {string[]} fileIds
   */
  static async removeFilesFromKnowledgeBase(knowledgeBaseId, fileIds) {
    const results = [];
    for (const fileId of fileIds) {
      results.push(await this.deleteFile(knowledgeBaseId, fileId));
    }
    return { success: true, data: results };
  }

  /**
   * 查询文件处理任务状态（同 getFileStatus）
   * @param {string} knowledgeBaseId
   * @param {string} fileId
   */
  static async getKnowledgeBaseJobStatus(knowledgeBaseId, fileId) {
    return this.getFileStatus(knowledgeBaseId, fileId);
  }

  /**
   * @deprecated 使用 getKnowledgeBasesByUserId() 代替
   */
  static async getFilesByUserId() {
    return this._unsupported();
  }

  static _unsupported() {
    throw new Error('该方法已弃用');
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
