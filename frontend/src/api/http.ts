import axios from "axios";
import type { ApiResponse } from "../types/recipe";

// axios 实例：baseURL 优先取 VITE_API_BASE 环境变量（部署时指向真实后端），本地开发默认 8080
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || "http://localhost:8080/api",
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
