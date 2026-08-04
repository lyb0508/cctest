import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// Vite 配置：
// - host: true 监听所有网卡（IPv4/IPv6 + 局域网），避免浏览器把 localhost 解析成 127.0.0.1 时连不上
// - proxy: 把 /api 转发到后端，前端走同源相对路径，局域网设备也能正常调接口（无需配 CORS）
export default defineConfig({
  plugins: [vue()],
  server: {
    host: true,
    port: 5173,
    headers: {
      "X-Content-Type-Options": "nosniff",
      "X-Frame-Options": "DENY",
      "Referrer-Policy": "no-referrer"
    },
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
});
