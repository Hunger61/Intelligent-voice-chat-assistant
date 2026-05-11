import { clearAuthToken, decodeUserIdFromToken, getAuthToken, setAuthToken } from './auth.js';

class UserService {
  /**
   * 用户注册
   * @param {Object} userData - 用户注册数据
   * @param {string} userData.name - 用户名
   * @param {string} userData.email - 用户邮箱
   * @param {string} userData.password - 用户密码
   */
  static async register(userData) {
    try {
      const response = await fetch('/api/public/user/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nickName: userData.name,
          email: userData.email,
          password: userData.password,
          verificationCode: userData.verificationCode
        })
      });

      return this._handleResponse(response);
    } catch (error) {
      console.error('Error registering user:', error);
      throw error;
    }
  }

  /**
   * 用户登录
   * @param {Object} credentials - 用户登录凭证
   * @param {string} credentials.email - 用户邮箱
   * @param {string} credentials.password - 用户密码
   */
  static async login(credentials) {
    try {
      const response = await fetch('/api/public/user/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: credentials.email,
          password: credentials.password
        })
      });
      const result = await this._handleResponse(response);
      const token = this._extractToken(result);
      if (token) {
        setAuthToken(token);
        return {
          success: true,
          data: {
            token,
            id: decodeUserIdFromToken(token),
            name: credentials.email
          }
        };
      }
      return result;
    } catch (error) {
      console.error('Error logging in:', error);
      throw error;
    }
  }

  static async getVerificationCode(email) {
    const params = new URLSearchParams({ email });
    const response = await fetch(`/api/public/user/getVerificationCode?${params.toString()}`, {
      method: 'POST'
    });
    return this._handleResponse(response);
  }

  static getCurrentUserFromToken() {
    const token = getAuthToken();
    if (!token) return null;
    const id = decodeUserIdFromToken(token);
    if (!id) return null;
    return { id, name: '已登录用户', token };
  }

  /**
   * 用户登出
   */
  static async logout() {
    try {
      const token = getAuthToken();
      const response = await fetch('/api/public/user/logout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Token: token } : {})
        }
      });
      const result = await this._handleResponse(response);
      clearAuthToken();
      return result;
    } catch (error) {
      console.error('Error logging out:', error);
      throw error;
    }
  }

  /**
   * 处理响应
   * @param {Response} response - fetch响应对象
   */
  static async _handleResponse(response) {
    // 首先检查HTTP状态码
    if (!response.ok) {
        // 处理401未授权错误
        if (response.status === 401) {
            clearAuthToken();
            window.location.href = '/static/';
            throw new Error('登录已过期，请重新登录');
        }
        
        const errorText = await response.text();
        if (!errorText) {
          throw new Error(`请求失败: ${response.status} - ${response.statusText}`);
        }
          let message = errorText;
        try {
          const errorData = JSON.parse(errorText);
            message = errorData.message || `请求失败: ${response.status}`;
        } catch {
            message = errorText || `请求失败: ${response.status}`;
        }
          throw new Error(message);
    }

    const text = await response.text();
    if (!text) {
      return { success: true, data: null };
    }

    try {
      const data = JSON.parse(text);
      if (data && typeof data === 'object' && Object.prototype.hasOwnProperty.call(data, 'success')) {
        return data;
      }
      if (typeof data === 'string') {
        return { success: true, data };
      }
      return { success: true, data };
    } catch {
      return { success: true, data: text };
    }
  }

  static _extractToken(result) {
    if (typeof result === 'string') return result;
    if (typeof result?.data === 'string') return result.data;
    if (typeof result?.data?.token === 'string') return result.data.token;
    return null;
  }
}



export default UserService;