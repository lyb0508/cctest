package com.example.foodai.recipe.service;

import com.example.foodai.recipe.dto.DishGuideRequest;
import com.example.foodai.recipe.vo.RecipeDetailResponse;

/**
 * 菜谱业务接口：定义生成与查询两个用例。
 */
public interface RecipeService {

    RecipeDetailResponse createDishGuide(DishGuideRequest request);

    RecipeDetailResponse getRecipeDetail(Long recipeId);
}
