<script setup lang="ts">
import type { RecipeIngredient } from "../types/recipe";

// 食材清单：左侧取食材名首字做徽标，右侧展示用量
defineProps<{
  ingredients: RecipeIngredient[];
}>();
</script>

<template>
  <section class="panel ingredient-panel">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Ingredients</p>
        <h3>食材清单</h3>
      </div>
      <span class="section-tag">下锅前准备</span>
    </div>
    <ul class="ingredient-list">
      <li v-for="ingredient in ingredients" :key="ingredient.name">
        <div class="ingredient-badge">{{ ingredient.name.slice(0, 1) }}</div>
        <span class="ingredient-name">{{ ingredient.name }}</span>
        <strong class="ingredient-amount">{{ ingredient.amount }}</strong>
      </li>
    </ul>
  </section>
</template>
