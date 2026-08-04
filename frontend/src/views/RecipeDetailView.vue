<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { getRecipeDetail } from "../api/recipe";
import IngredientList from "../components/IngredientList.vue";
import RecipeHeader from "../components/RecipeHeader.vue";
import RecipeStepList from "../components/RecipeStepList.vue";
import type { RecipeDetailResponse } from "../types/recipe";

const route = useRoute();
const recipe = ref<RecipeDetailResponse | null>(null);
const loading = ref(true);
const errorMessage = ref("");

// 进入页面即按路由参数加载菜谱详情；错误信息来自 http 拦截器透传的后端 message
onMounted(async () => {
  try {
    const recipeId = String(route.params.recipeId);
    recipe.value = await getRecipeDetail(recipeId);
  } catch (error) {
    errorMessage.value = error instanceof Error && error.message ? error.message : "获取菜谱详情失败。";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="detail">
    <p v-if="loading" class="status-text">加载中...</p>
    <p v-else-if="errorMessage" class="error-text">{{ errorMessage }}</p>
    <template v-else-if="recipe">
      <RecipeHeader :recipe="recipe" />
      <section class="card intro-card">
        <p class="eyebrow">Intro</p>
        <h3 class="block-title">菜谱简介</h3>
        <p class="intro-text">{{ recipe.recipe.description }}</p>
      </section>
      <div class="split">
        <IngredientList :ingredients="recipe.recipe.ingredients" />
        <RecipeStepList :steps="recipe.recipe.steps" />
      </div>
      <section class="card tips-card">
        <p class="eyebrow">Tips</p>
        <h3 class="block-title">制作提示</h3>
        <ul class="tips-list">
          <li v-for="tip in recipe.recipe.tips" :key="tip">{{ tip }}</li>
        </ul>
      </section>
      <router-link class="back-link" to="/">← 再来一道</router-link>
    </template>
  </div>
</template>