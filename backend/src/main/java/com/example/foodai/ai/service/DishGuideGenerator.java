package com.example.foodai.ai.service;

import com.example.foodai.ai.model.AiDishGuideRequest;
import com.example.foodai.ai.model.AiRecipeResult;

/**
 * AI 生成器接口：可插拔实现（本地示例 / Ollama 真实模型），
 * 具体走哪个实现由 application.yml 的 foodai.ai.provider 决定。
 */
public interface DishGuideGenerator {

    AiRecipeResult generate(AiDishGuideRequest request);
}
