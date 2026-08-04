import axios from "axios";
import type { ApiResponse } from "../types/recipe";

// axios 实例：
// - baseURL 默认用相对路径 /api，经 Vite 代理转发到后端（局域网设备也能用，不会指向设备自己的 localhost）
// - 部署时可用 VITE_API_BASE 环境变量覆盖为真实后端地址
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || "/api",
  timeout: 15000
});

// 响应拦截器：统一处理业务错误码，让调用方只需 catch(error) 并展示 error.message
http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown>;
    // 后端返回 HTTP 200 但业务失败（code!==0）时，这里主动 reject，避免上层拿到 null 数据
    if (body && typeof body.code === "number" && body.code !== 0) {
      return Promise.reject(new Error(body.message || "Request failed"));
    }
    return response;
  },
  // HTTP 层错误（4xx/5xx）：优先透出后端 ApiResponse 里的 message，取不到再用通用文案
  (error) => {
    const message = error?.response?.data?.message || error?.message || "网络错误，请稍后重试";
    return Promise.reject(new Error(message));
  }
);

export default http;
