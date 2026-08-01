package com.example.foodai.recipe.service.impl;

import com.example.foodai.ai.service.DishGuideAiService;
import com.example.foodai.common.ResourceNotFoundException;
import com.example.foodai.recipe.dto.DishGuideRequest;
import com.example.foodai.recipe.service.RecipeService;
import com.example.foodai.recipe.vo.RecipeDetailResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final DishGuideAiService dishGuideAiService;
    private final AtomicLong recipeIdGenerator = new AtomicLong(1000);
    private final Map<Long, RecipeDetailResponse> recipeStore = new ConcurrentHashMap<>();

    public RecipeServiceImpl(DishGuideAiService dishGuideAiService) {
        this.dishGuideAiService = dishGuideAiService;
    }

    @Override
    public RecipeDetailResponse createDishGuide(DishGuideRequest request) {
        Long recipeId = recipeIdGenerator.incrementAndGet();
        RecipeDetailResponse generated = dishGuideAiService.generateDishGuide(recipeId, request);
        recipeStore.put(recipeId, generated);
        return generated;
    }

    @Override
    public RecipeDetailResponse getRecipeDetail(Long recipeId) {
        RecipeDetailResponse cached = recipeStore.get(recipeId);
        if (cached != null) {
            return cached;
        }
        throw new ResourceNotFoundException("Recipe not found: " + recipeId);
    }
}