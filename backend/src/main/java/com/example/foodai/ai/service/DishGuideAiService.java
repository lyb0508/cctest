package com.example.foodai.ai.service;

import com.example.foodai.ai.model.AiDishGuideRequest;
import com.example.foodai.ai.model.AiRecipeResult;
import com.example.foodai.recipe.dto.DishGuideRequest;
import com.example.foodai.recipe.vo.RecipeData;
import com.example.foodai.recipe.vo.RecipeDetailResponse;
import org.springframework.stereotype.Service;

@Service
public class DishGuideAiService {

    private final DishGuideGenerator dishGuideGenerator;

    public DishGuideAiService(DishGuideGenerator dishGuideGenerator) {
        this.dishGuideGenerator = dishGuideGenerator;
    }

    public RecipeDetailResponse generateDishGuide(Long recipeId, DishGuideRequest request) {
        AiRecipeResult generated = dishGuideGenerator.generate(new AiDishGuideRequest(
                request.dishName().trim(),
                request.servings(),
                request.difficulty(),
                request.flavor(),
                request.additionalNote()
        ));

        RecipeData recipe = new RecipeData(
                generated.title(),
                generated.description(),
                generated.servings(),
                generated.prepTime(),
                generated.cookTime(),
                generated.totalTime(),
                generated.difficulty(),
                generated.ingredients(),
                generated.steps(),
                generated.tips()
        );

        return new RecipeDetailResponse(
                recipeId,
                "DISH_GUIDE",
                "新手版" + request.dishName().trim(),
                "按菜名生成的结构化做法，可直接用于前端展示。",
                recipe
        );
    }

}
