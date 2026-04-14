class KnowledgeService {
  static _unsupported() {
    throw new Error('知识库相关接口在当前后端版本未实现');
  }

  static async uploadFile() { return this._unsupported(); }
  static async getFileStatus() { return this._unsupported(); }
  static async deleteFile() { return this._unsupported(); }
  static async getFilesByUserId() { return this._unsupported(); }
  static async createKnowledgeBase() { return this._unsupported(); }
  static async getKnowledgeBasesByUserId() { return this._unsupported(); }
  static async getKnowledgeBaseFiles() { return this._unsupported(); }
  static async addFilesToKnowledgeBase() { return this._unsupported(); }
  static async removeFilesFromKnowledgeBase() { return this._unsupported(); }
  static async getKnowledgeBaseJobStatus() { return this._unsupported(); }
  static async deleteKnowledgeBase() { return this._unsupported(); }
}
export default KnowledgeService;