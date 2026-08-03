package com.example.foodai.recipe.vo;

/**
 * 单条食材：名称 + 用量。
 */
public record RecipeIngredient(
        String name,
        String amount
) {
}
