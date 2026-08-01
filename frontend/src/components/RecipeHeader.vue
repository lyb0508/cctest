<script setup lang="ts">
import type { RecipeDetailResponse } from "../types/recipe";

defineProps<{
  recipe: RecipeDetailResponse;
}>();
</script>

<template>
  <section class="panel">
    <p class="eyebrow">{{ recipe.generationType }}</p>
    <h2>{{ recipe.title }}</h2>
    <p class="recipe-summary">{{ recipe.summary }}</p>
    <div class="meta-row">
      <span>人数 {{ recipe.recipe.servings }}</span>
      <span>准备 {{ recipe.recipe.prepTime }} 分钟</span>
      <span>烹饪 {{ recipe.recipe.cookTime }} 分钟</span>
      <span>总时长 {{ recipe.recipe.totalTime }} 分钟</span>
      <span>难度 {{ recipe.recipe.difficulty }}</span>
    </div>
  </section>
</template>
