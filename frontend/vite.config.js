import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/static/',
  plugins: [vue()],
  server: {

    // port: 5173, // 前端开发服务器端口（默认5173）
    proxy: {
      // 1. 代理所有以 `/api` 开头的请求
      '/api': {
        target: 'http://localhost:8080', // 后端服务器地址（目标地址）
        changeOrigin: true, // 关键：是否修改请求头的 Origin 为 target 域名
        // rewrite: (path) => path.replace(/^\/api/, ''), // 可选：去掉路径中的 /api 前缀
        // 其他可选配置
        // ws: true, // 代理 WebSocket（如实时通信）
        secure: false, // 若 target 是 https，是否验证 SSL 证书（开发环境建议 false）
      },
    }
  },
})
