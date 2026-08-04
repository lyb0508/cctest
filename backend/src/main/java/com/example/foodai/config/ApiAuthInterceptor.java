package com.example.foodai.config;

import com.example.foodai.common.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API Key 鉴权拦截器（作用于 /api/**）。
 * 配置了 foodai.security.api-key 后，请求必须携带 X-API-Key 请求头且值一致，否则抛 401；
 * api-key 留空则跳过（本地开发默认）。
 * 限流已移到 RecipeController（仅对通过校验的生成请求计数，见 RateLimiter）。
 */
@Component
public class ApiAuthInterceptor implements HandlerInterceptor {

    private final ApiSecurityProperties securityProperties;

    public ApiAuthInterceptor(ApiSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 浏览器跨域预检请求不会携带自定义请求头，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (StringUtils.hasText(securityProperties.getApiKey())) {
            String provided = request.getHeader("X-API-Key");
            if (!securityProperties.getApiKey().equals(provided)) {
                throw new UnauthorizedException("Missing or invalid X-API-Key");
            }
        }
        return true;
    }
}