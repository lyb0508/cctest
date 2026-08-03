package com.example.foodai.recipe.controller;

import com.example.foodai.common.ApiResponse;
import com.example.foodai.recipe.dto.DishGuideRequest;
import com.example.foodai.recipe.service.RecipeService;
import com.example.foodai.recipe.vo.RecipeDetailResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜谱接口：生成菜谱 + 按 id 查询详情，统一返回 ApiResponse。
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // 生成菜谱：入参走 Bean Validation（@Valid），失败返回 4000 错误码
    @PostMapping("/dish-guide")
    public ApiResponse<RecipeDetailResponse> createDishGuide(@Valid @RequestBody DishGuideRequest request) {
        return ApiResponse.success(recipeService.createDishGuide(request));
    }

    // 查询详情：不存在的 id 抛 ResourceNotFoundException -> 404
    @GetMapping("/{recipeId}")
    public ApiResponse<RecipeDetailResponse> getRecipeDetail(@PathVariable Long recipeId) {
        return ApiResponse.success(recipeService.getRecipeDetail(recipeId));
    }
}
