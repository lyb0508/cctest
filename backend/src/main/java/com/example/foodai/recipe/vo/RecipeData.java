package com.example.foodai.recipe.vo;

import java.util.List;

public record RecipeData(
        String title,
        String description,
        Integer servings,
        Integer prepTime,
        Integer cookTime,
        Integer totalTime,
        String difficulty,
        List<RecipeIngredient> ingredients,
        List<RecipeStep> steps,
        List<String> tips
) {
}
