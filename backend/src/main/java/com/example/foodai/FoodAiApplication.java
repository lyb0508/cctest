package com.example.foodai;

import com.example.foodai.config.AiProperties;
import com.example.foodai.config.ApiSecurityProperties;
import com.example.foodai.config.RateLimitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// 应用启动类：@EnableConfigurationProperties 开启 foodai.* 配置的属性绑定（见 config/AiProperties、config/WebConfig）
@SpringBootApplication
@EnableConfigurationProperties({AiProperties.class, ApiSecurityProperties.class, RateLimitProperties.class})
public class FoodAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodAiApplication.class, args);
    }
}
