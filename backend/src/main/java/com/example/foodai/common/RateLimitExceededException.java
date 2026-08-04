package com.example.foodai.common;

/**
 * 触发限流异常：请求频率超过阈值时抛出，
 * 由 GlobalExceptionHandler 统一映射为 HTTP 429 + code 4290。
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}
