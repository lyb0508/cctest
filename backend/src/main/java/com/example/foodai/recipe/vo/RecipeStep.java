package com.example.foodai.recipe.vo;

public record RecipeStep(
        Integer stepNo,
        String title,
        String content,
        Integer duration,
        String note
) {
}
