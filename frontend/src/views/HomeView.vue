<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { createDishGuide } from "../api/recipe";
import DishGuideForm from "../components/DishGuideForm.vue";
import type { DishGuideRequest } from "../types/recipe";

const router = useRouter();
const loading = ref(false);
const errorMessage = ref("");

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
  <div class="page-grid">
    <DishGuideForm @submit="handleSubmit" />
    <section class="panel accent-panel">
      <p class="eyebrow">当前范围</p>
      <h2>第一阶段目标</h2>
      <p>用户输入一道菜名，页面返回结构化做法并展示出来。</p>
      <ul class="bullet-list">
        <li>后端接口：POST /api/recipes/dish-guide</li>
        <li>前端页面：输入表单 + 结果详情页</li>
        <li>AI 接入：当前调用本地 Ollama 模型（qwen2.5:3b）生成</li>
      </ul>
      <div class="feature-strip">
        <div>
          <strong>结构化结果</strong>
          <span>标题、食材、步骤、提示分区展示</span>
        </div>
        <div>
          <strong>前后端分离</strong>
          <span>页面交互与 AI 调用链路已拆开</span>
        </div>
      </div>
      <div class="mvp-note">
        <strong>MVP 核心闭环</strong>
        <p>先把“一道菜怎么做”这件事做顺，再扩展到按食材生成、历史记录、追问对话。</p>
      </div>
      <p v-if="loading" class="status-text">正在生成做法...</p>
      <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
    </section>
  </div>
</template>
