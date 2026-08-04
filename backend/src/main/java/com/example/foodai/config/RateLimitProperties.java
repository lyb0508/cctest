package com.example.foodai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * foodai.rate-limit.* 配置：对"生成菜谱"接口（AI 调用，计算昂贵）做客户端 IP 级限流。
 */
@ConfigurationProperties(prefix = "foodai.rate-limit")
public class RateLimitProperties {

    /** 每个 IP 每分钟允许的生成请求数 */
    private int requestsPerMinute = 10;

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }
}
