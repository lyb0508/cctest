package com.example.foodai.ai.model;

import com.example.foodai.recipe.vo.RecipeIngredient;
import com.example.foodai.recipe.vo.RecipeStep;

import java.util.List;

public record AiRecipeResult(
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
