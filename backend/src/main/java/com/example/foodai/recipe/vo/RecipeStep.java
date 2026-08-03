package com.example.foodai.recipe.vo;

/**
 * 单条制作步骤：序号/标题/内容/预计时长(分钟)/小提示。
 */
public record RecipeStep(
        Integer stepNo,
        String title,
        String content,
        Integer duration,
        String note
) {
}
