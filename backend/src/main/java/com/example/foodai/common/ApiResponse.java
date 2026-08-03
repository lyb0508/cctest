package com.example.foodai.common;

/**
 * 统一响应包装：所有接口返回 {code, message, data}。
 * code=0 表示成功，非 0 错误码含义见 GlobalExceptionHandler。
 */
public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }
}
