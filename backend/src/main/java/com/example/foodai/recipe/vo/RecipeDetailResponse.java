package com.example.foodai.recipe.vo;

/**
 * 对外返回的菜谱详情：外层元信息 + 内层 RecipeData 结构化内容。
 */
public record RecipeDetailResponse(
        Long recipeId,
        String generationType,
        String title,
        String summary,
        RecipeData recipe
) {
}
