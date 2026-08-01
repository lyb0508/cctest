import axios from "axios";
import type { ApiResponse } from "../types/recipe";

const http = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 15000
});

http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown>;
    if (body && typeof body.code === "number" && body.code !== 0) {
      return Promise.reject(new Error(body.message || "Request failed"));
    }
    return response;
  },
  (error) => {
    const message = error?.response?.data?.message || error?.message || "网络错误，请稍后重试";
    return Promise.reject(new Error(message));
  }
);

export default http;