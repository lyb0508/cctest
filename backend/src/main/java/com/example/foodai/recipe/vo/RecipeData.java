package com.example.foodai.recipe.vo;

import java.util.List;

/**
 * 菜谱结构化内容：标题/描述/份量/时间/难度/食材/步骤/提示。
 */
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
