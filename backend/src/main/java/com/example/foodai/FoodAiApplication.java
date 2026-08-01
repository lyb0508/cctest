package com.example.foodai;

import com.example.foodai.config.AiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiProperties.class)
public class FoodAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodAiApplication.class, args);
    }
}
