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

/**
 * 菜谱服务实现（MVP 取舍：无数据库）。
 * - 生成的菜谱保存在内存 ConcurrentHashMap 中，进程重启即丢失
 * - ID 从 1000 起递增（AtomicLong 保证并发安全）
 * 后续接入数据库（如 JPA/MyBatis）时，仅需替换本实现与数据模型。
 */
@Service
public class RecipeServiceImpl implements RecipeService {

    private final DishGuideAiService dishGuideAiService;
    private final AtomicLong recipeIdGenerator = new AtomicLong(1000);
    private final Map<Long, RecipeDetailResponse> recipeStore = new ConcurrentHashMap<>();

    public RecipeServiceImpl(DishGuideAiService dishGuideAiService) {
        this.dishGuideAiService = dishGuideAiService;
    }

    // 生成流程：分配 id -> 调用 AI 生成 -> 存入内存 -> 返回
    @Override
    public RecipeDetailResponse createDishGuide(DishGuideRequest request) {
        Long recipeId = recipeIdGenerator.incrementAndGet();
        RecipeDetailResponse generated = dishGuideAiService.generateDishGuide(recipeId, request);
        recipeStore.put(recipeId, generated);
        return generated;
    }

    // 查询流程：命中内存缓存直接返回；未命中抛 404，避免返回"示例菜谱"误导用户
    @Override
    public RecipeDetailResponse getRecipeDetail(Long recipeId) {
        RecipeDetailResponse cached = recipeStore.get(recipeId);
        if (cached != null) {
            return cached;
        }
        throw new ResourceNotFoundException("Recipe not found: " + recipeId);
    }
}
