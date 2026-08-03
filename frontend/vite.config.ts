import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// Vite 配置：本地 dev server 固定 5173 端口；
// 如需代理后端（避免 CORS），可在此增加 server.proxy 配置指向 http://localhost:8080
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173
  }
});
