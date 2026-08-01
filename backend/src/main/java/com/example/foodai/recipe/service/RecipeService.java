package com.example.foodai.recipe.service;

import com.example.foodai.recipe.dto.DishGuideRequest;
import com.example.foodai.recipe.vo.RecipeDetailResponse;

public interface RecipeService {

    RecipeDetailResponse createDishGuide(DishGuideRequest request);

    RecipeDetailResponse getRecipeDetail(Long recipeId);
}
