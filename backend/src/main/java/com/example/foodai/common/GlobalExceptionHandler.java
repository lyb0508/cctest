package com.example.foodai.common;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理：把各类异常统一转换为 ApiResponse + 合适的 HTTP 状态码。
 * 错误码约定：
 * 4000 参数校验失败 / 4001 约束违规 / 4002 请求体 JSON 非法 / 4003 路径参数类型错误
 * 4004 媒体类型不支持(415) / 4040 资源不存在 / 5000 未知异常 / 5001 业务异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // @Valid 触发的字段校验失败（如 @NotBlank、@Size）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException exception) {
        // 把多个字段错误拼接为一条提示，如 "dishName: dishName is required"
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return new ApiResponse<>(4000, message, null);
    }

    // 方法参数上的约束校验失败（路径/查询参数）
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleConstraintViolation(ConstraintViolationException exception) {
        return new ApiResponse<>(4001, exception.getMessage(), null);
    }

    // 请求体不是合法 JSON 或无法反序列化
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleUnreadable(HttpMessageNotReadableException exception) {
        return new ApiResponse<>(4002, "Malformed request body", null);
    }

    // 路径/查询参数类型不匹配，如 /api/recipes/abc（期望数字 id）
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return new ApiResponse<>(4003, "Invalid parameter: " + exception.getName(), null);
    }

    // Content-Type 不被支持（如用表单方式调 JSON 接口）
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResponse<Void> handleMediaType(HttpMediaTypeNotSupportedException exception) {
        return new ApiResponse<>(4004, "Unsupported media type", null);
    }

    // 资源不存在（如查询不存在的菜谱）
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(ResourceNotFoundException exception) {
        return new ApiResponse<>(4040, exception.getMessage(), null);
    }

    // API Key 缺失/不匹配（未授权）
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUnauthorized(UnauthorizedException exception) {
        return new ApiResponse<>(4010, exception.getMessage(), null);
    }

    // 触发限流（请求过于频繁）
    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiResponse<Void> handleRateLimit(RateLimitExceededException exception) {
        return new ApiResponse<>(4290, exception.getMessage(), null);
    }

    // 业务异常：服务端配置/能力问题（如 AI 未配置、AI 返回无法解析）
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleBusiness(BusinessException exception) {
        // 不把内部细节（配置缺失、AI 失败原因）透传给客户端，明细只留日志
        log.warn("Business exception: {}", exception.getMessage());
        return new ApiResponse<>(5001, "Business error, please try again later", null);
    }

    // 兜底：未知异常绝不把堆栈暴露给客户端，只记录日志并返回通用提示
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleUnexpected(Exception exception) {
        log.error("Unexpected server error", exception);
        return new ApiResponse<>(5000, "Unexpected server error", null);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
