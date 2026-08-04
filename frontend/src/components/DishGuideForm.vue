<script setup lang="ts">
import { reactive, ref } from "vue";
import type { DishGuideRequest } from "../types/recipe";

// 表单组件：只负责收集输入并 emit，提交逻辑由父组件处理
defineProps<{
  loading?: boolean; // 父组件传入的"生成中"状态，用于禁用按钮防止重复提交
}>();

const emit = defineEmits<{
  submit: [payload: DishGuideRequest];
}>();

// 表单模型：默认 2 人份 / 新手难度；与后端校验规则（servings 1~10）对应
const form = reactive<DishGuideRequest>({
  dishName: "",
  servings: 2,
  difficulty: "BEGINNER",
  flavor: "家常",
  additionalNote: ""
});

const formError = ref("");

function handleSubmit() {
  if (!form.dishName.trim()) {
    // 空菜名要有明确反馈，不能静默无反应
    formError.value = "请输入菜名";
    return;
  }
  formError.value = "";
  emit("submit", {
    ...form,
    dishName: form.dishName.trim(),
    additionalNote: form.additionalNote?.trim()
  });
}
</script>

<template>
  <form class="dish-form" @submit.prevent="handleSubmit">
    <div class="search-row">
      <input
        v-model="form.dishName"
        class="field search-input"
        placeholder="例如：红烧肉、宫保鸡丁、番茄炒蛋"
        aria-label="菜名"
        @input="formError = ''"
      />
      <button class="btn-primary" type="submit" :disabled="loading">
        {{ loading ? "生成中…" : "生成做法" }}
      </button>
    </div>
    <p v-if="formError" class="form-error" role="alert">{{ formError }}</p>
    <div class="form-grid">
      <label>
        <span>人数</span>
        <input v-model.number="form.servings" class="field" type="number" min="1" max="10" />
      </label>
      <label>
        <span>难度</span>
        <select v-model="form.difficulty" class="field">
          <option value="BEGINNER">新手</option>
          <option value="INTERMEDIATE">进阶</option>
          <option value="ADVANCED">熟练</option>
        </select>
      </label>
      <label>
        <span>口味</span>
        <input v-model="form.flavor" class="field" placeholder="少辣、家常" />
      </label>
      <label class="full">
        <span>补充说明</span>
        <textarea
          v-model="form.additionalNote"
          class="field"
          rows="3"
          placeholder="例如：适合新手，没有烤箱"
        />
      </label>
    </div>
    <div class="quick-tags">
      <span>快速灵感</span>
      <button type="button" @click="form.dishName = '宫保鸡丁'">宫保鸡丁</button>
      <button type="button" @click="form.dishName = '红烧肉'">红烧肉</button>
      <button type="button" @click="form.dishName = '番茄炒蛋'">番茄炒蛋</button>
    </div>
  </form>
</template>