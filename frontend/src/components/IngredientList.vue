<script setup lang="ts">
import { computed, ref } from "vue";
import type { RecipeIngredient } from "../types/recipe";

// 食材清单：点击可勾选"已备齐"，右上角显示备齐进度（贴合真实备料场景）
const props = defineProps<{
  ingredients: RecipeIngredient[];
}>();

const checked = ref<Set<number>>(new Set());
const readyCount = computed(() => checked.value.size);

function toggle(index: number) {
  const next = new Set(checked.value);
  if (next.has(index)) {
    next.delete(index);
  } else {
    next.add(index);
  }
  checked.value = next;
}
</script>

<template>
  <section class="card">
    <header class="card-head">
      <div>
        <p class="eyebrow">Ingredients</p>
        <h3>食材清单</h3>
      </div>
      <span class="count-pill" :class="{ done: readyCount === ingredients.length }">
        {{ readyCount }}/{{ ingredients.length }} 已备齐
      </span>
    </header>
    <ul class="check-list">
      <li v-for="(ingredient, i) in ingredients" :key="`${ingredient.name}-${i}`">
        <button
          type="button"
          class="check-row"
          :class="{ checked: checked.has(i) }"
          :aria-pressed="checked.has(i)"
          @click="toggle(i)"
        >
          <span class="check-box" aria-hidden="true">✓</span>
          <span class="ingredient-name">{{ ingredient.name }}</span>
          <strong class="ingredient-amount">{{ ingredient.amount }}</strong>
        </button>
      </li>
    </ul>
  </section>
</template>