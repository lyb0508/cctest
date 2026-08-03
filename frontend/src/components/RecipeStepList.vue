<script setup lang="ts">
import type { RecipeStep } from "../types/recipe";

// 制作步骤列表：按 stepNo 排序展示，含预计时长与提示（时长由后端归一化兜底，不会为空）
defineProps<{
  steps: RecipeStep[];
}>();
</script>

<template>
  <section class="panel step-panel">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Steps</p>
        <h3>制作步骤</h3>
      </div>
      <span class="section-tag">按顺序完成</span>
    </div>
    <ol class="steps">
      <li v-for="step in steps" :key="step.stepNo" class="step-item">
        <div class="step-index">{{ step.stepNo }}</div>
        <div class="step-body">
          <h4>{{ step.title }}</h4>
          <p>{{ step.content }}</p>
          <p class="step-note">预计 {{ step.duration }} 分钟<span v-if="step.note"> · {{ step.note }}</span></p>
        </div>
      </li>
    </ol>
  </section>
</template>
