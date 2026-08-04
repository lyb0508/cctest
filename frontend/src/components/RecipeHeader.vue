<script setup lang="ts">
import { computed } from "vue";
import type { RecipeDetailResponse } from "../types/recipe";

// 菜谱头部：标题/摘要 + 元信息（份量、时间、难度）
const props = defineProps<{
  recipe: RecipeDetailResponse;
}>();

// 把后端的英文难度枚举映射为中文展示
const difficultyLabel = computed(
  () =>
    ({
      BEGINNER: "新手",
      INTERMEDIATE: "进阶",
      ADVANCED: "熟练"
    })[props.recipe.recipe.difficulty] ?? props.recipe.recipe.difficulty
);
</script>

<template>
  <header class="detail-head">
    <p class="eyebrow">{{ recipe.generationType }}</p>
    <h1 class="detail-title">{{ recipe.title }}</h1>
    <p class="detail-summary">{{ recipe.summary }}</p>
    <div class="meta-row">
      <span class="meta-chip">份量 <strong>{{ recipe.recipe.servings }} 人</strong></span>
      <span class="meta-chip">准备 <strong class="time">{{ recipe.recipe.prepTime }} 分</strong></span>
      <span class="meta-chip">烹饪 <strong class="time">{{ recipe.recipe.cookTime }} 分</strong></span>
      <span class="meta-chip">总计 <strong class="time">{{ recipe.recipe.totalTime }} 分</strong></span>
      <span class="meta-chip">难度 <strong>{{ difficultyLabel }}</strong></span>
    </div>
  </header>
</template>