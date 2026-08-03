package com.example.foodai.common;

/**
 * 资源不存在异常：查询不存在的菜谱时抛出，
 * 由 GlobalExceptionHandler 统一映射为 HTTP 404 + code 4040。
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
