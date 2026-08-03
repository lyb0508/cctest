package com.example.foodai.ai.model;

/**
 * AI 层专用入参：与业务层 recipe.dto.DishGuideRequest 解耦，
 * 避免 AI 生成器直接依赖对外 DTO，后续替换模型/提供商时更干净。
 */
public record AiDishGuideRequest(
        String dishName,
        Integer servings,
        String difficulty,
        String flavor,
        String additionalNote
) {
}
