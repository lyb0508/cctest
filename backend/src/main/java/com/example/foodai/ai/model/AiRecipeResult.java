package com.example.foodai.ai.model;

import com.example.foodai.recipe.vo.RecipeIngredient;
import com.example.foodai.recipe.vo.RecipeStep;

import java.util.List;

/**
 * AI 返回的结构化菜谱：由模型输出的 JSON 反序列化而来。
 * 字段与前端 RecipeData 一一对应，组装成业务响应前可做校验/归一化（见 OllamaDishGuideGenerator）。
 */
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
