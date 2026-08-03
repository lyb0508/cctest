package com.example.foodai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域(CORS)配置。
 * 默认只放行本地前端开发服务器（localhost:5173）；
 * 生产环境应通过 foodai.cors.allowed-origins 配置真实来源白名单，不要使用 "*" 通配。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 允许的来源白名单，来自配置项 foodai.cors.allowed-origins（逗号分隔可配多个） */
    private final String[] allowedOrigins;

    public WebConfig(@Value("${foodai.cors.allowed-origins:http://localhost:5173}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 仅对 /api/** 开放跨域；方法集含 OPTIONS（浏览器预检请求）
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
