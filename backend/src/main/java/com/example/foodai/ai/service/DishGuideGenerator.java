package com.example.foodai.ai.service;

import com.example.foodai.ai.model.AiDishGuideRequest;
import com.example.foodai.ai.model.AiRecipeResult;

public interface DishGuideGenerator {

    AiRecipeResult generate(AiDishGuideRequest request);
}
