package com.example.foodai.common;

/**
 * 未授权/凭证无效异常：API Key 缺失或不匹配时抛出，
 * 由 GlobalExceptionHandler 统一映射为 HTTP 401 + code 4010。
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
