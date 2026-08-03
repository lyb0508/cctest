package com.example.foodai.recipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 生成菜谱的入参 DTO。
 * 校验要点：
 * - dishName 必填且限长，防止超长输入把提示词撑得过大（也缩小提示注入的攻击面）
 * - servings 限制 1~10；其余文本字段也做了长度上限
 */
public record DishGuideRequest(
        @NotBlank(message = "dishName is required")
        @Size(max = 50, message = "dishName must be at most 50 characters")
        String dishName,
        @Min(value = 1, message = "servings must be at least 1")
        @Max(value = 10, message = "servings must be at most 10")
        Integer servings,
        @Size(max = 20, message = "difficulty must be at most 20 characters")
        String difficulty,
        @Size(max = 50, message = "flavor must be at most 50 characters")
        String flavor,
        @Size(max = 200, message = "additionalNote must be at most 200 characters")
        String additionalNote
) {
}
