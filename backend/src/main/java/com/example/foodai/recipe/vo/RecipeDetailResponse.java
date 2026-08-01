package com.example.foodai.recipe.vo;

public record RecipeDetailResponse(
        Long recipeId,
        String generationType,
        String title,
        String summary,
        RecipeData recipe
) {
}
