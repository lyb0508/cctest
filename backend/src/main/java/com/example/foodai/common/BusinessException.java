package com.example.foodai.common;

/**
 * 业务异常：表示服务端能力/配置类问题（如 AI 未配置、AI 返回无法解析），
 * 由 GlobalExceptionHandler 统一映射为 HTTP 500 + code 5001。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
