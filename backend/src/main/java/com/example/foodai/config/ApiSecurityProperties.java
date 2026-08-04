package com.example.foodai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * foodai.security.* 配置：简单的 API Key 鉴权。
 * api-key 为空表示不启用鉴权（本地开发默认）；
 * 启用后所有 /api/** 请求必须携带 X-API-Key 请求头，否则返回 401。
 */
@ConfigurationProperties(prefix = "foodai.security")
public class ApiSecurityProperties {

    /** API Key；留空 = 不鉴权 */
    private String apiKey = "";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
