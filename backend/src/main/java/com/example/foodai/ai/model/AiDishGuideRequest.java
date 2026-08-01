package com.example.foodai.ai.model;

public record AiDishGuideRequest(
        String dishName,
        Integer servings,
        String difficulty,
        String flavor,
        String additionalNote
) {
}
