package com.example.foodai.recipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DishGuideRequest(
        @NotBlank(message = "dishName is required")
        String dishName,
        @Min(value = 1, message = "servings must be at least 1")
        @Max(value = 10, message = "servings must be at most 10")
        Integer servings,
        String difficulty,
        String flavor,
        String additionalNote
) {
}
