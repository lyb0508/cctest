<script setup lang="ts">
import { onUnmounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { createDishGuide } from "../api/recipe";
import DishGuideForm from "../components/DishGuideForm.vue";
import type { DishGuideRequest } from "../types/recipe";

const router = useRouter();
const loading = ref(false);
const errorMessage = ref("");

// 生成中的阶段性提示（真实生成约 10s，让用户知道在做什么）
const loadingMessages = [
  "正在识别菜名…",
  "正在生成食材清单…",
  "正在拆分做法步骤…",
  "正在整理制作提示…"
];
const loadingStep = ref(loadingMessages[0]);
let loadingTimer: number | undefined;

watch(loading, (value) => {
  if (value) {
    let i = 0;
    loadingStep.value = loadingMessages[0];
    loadingTimer = window.setInterval(() => {
      i = (i + 1) % loadingMessages.length;
      loadingStep.value = loadingMessages[i];
    }, 2600);
  } else if (loadingTimer) {
    window.clearInterval(loadingTimer);
    loadingTimer = undefined;
  }
});
onUnmounted(() => {
  if (loadingTimer) window.clearInterval(loadingTimer);
});

// 提交表单：调用生成接口后跳转到详情页；失败时展示拦截器透传的真实错误信息
async function handleSubmit(payload: DishGuideRequest) {
  loading.value = true;
  errorMessage.value = "";
  try {
    const recipe = await createDishGuide(payload);
    await router.push(`/recipes/${recipe.recipeId}`);
  } catch (error) {
    errorMessage.value = error instanceof Error && error.message ? error.message : "生成做法失败，请确认后端服务是否已启动。";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="page">
    <section class="card home-panel">
      <header class="panel-head">
        <p class="eyebrow">Dish Guide</p>
        <h2 class="panel-title">今天想吃什么？</h2>
        <p class="panel-sub">输入菜名，我给你一份可以照着做的清单式做法。</p>
      </header>
      <DishGuideForm @submit="handleSubmit" :loading="loading" />
      <div v-if="loading" class="loading-panel" role="status">
        <span class="spinner" aria-hidden="true"></span>
        <span class="loading-text">{{ loadingStep }}</span>
      </div>
      <p v-else-if="errorMessage" class="error-text" role="alert">{{ errorMessage }}</p>
    </section>
    <p class="how-strip">食材清单 · 分步做法（含用时） · 制作提示</p>
  </div>
</template>