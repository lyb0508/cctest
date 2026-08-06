package com.example.foodai.recipe.service.impl;

import com.example.foodai.ai.service.DishGuideAiService;
import com.example.foodai.common.ResourceNotFoundException;
import com.example.foodai.recipe.dto.DishGuideRequest;
import com.example.foodai.recipe.entity.RecipeEntity;
import com.example.foodai.recipe.repository.RecipeRepository;
import com.example.foodai.recipe.service.RecipeService;
import com.example.foodai.recipe.vo.RecipeDetailResponse;
import org.springframework.stereotype.Service;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final DishGuideAiService dishGuideAiService;
    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(DishGuideAiService dishGuideAiService, RecipeRepository recipeRepository) {
        this.dishGuideAiService = dishGuideAiService;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public RecipeDetailResponse createDishGuide(DishGuideRequest request) {
        RecipeDetailResponse generated = dishGuideAiService.generateDishGuide(0L, request);

        RecipeEntity entity = new RecipeEntity();
        entity.setGenerationType(generated.generationType());
        entity.setTitle(generated.title());
        entity.setSummary(generated.summary());
        entity.setRecipeData(generated.recipe());

        RecipeEntity saved = recipeRepository.save(entity);
        return new RecipeDetailResponse(
                saved.getId(),
                saved.getGenerationType(),
                saved.getTitle(),
                saved.getSummary(),
                saved.getRecipeData()
        );
    }

    @Override
    public RecipeDetailResponse getRecipeDetail(Long recipeId) {
        RecipeEntity entity = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId));
        return new RecipeDetailResponse(
                entity.getId(),
                entity.getGenerationType(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getRecipeData()
        );
    }
}