<script setup lang="ts">
import { reactive } from "vue";
import type { DishGuideRequest } from "../types/recipe";

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

// 菜名为空时不触发提交（由父组件展示统一错误即可）
function handleSubmit() {
  if (!form.dishName.trim()) {
    return;
  }
  emit("submit", {
    ...form,
    dishName: form.dishName.trim(),
    additionalNote: form.additionalNote?.trim()
  });
}
</script>

<template>
  <section class="panel hero-form-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Dish Guide</p>
        <h2>按菜名问做法</h2>
        <p class="form-intro">想做什么菜，直接输入菜名，我们先把做法清晰地拆出来。</p>
      </div>
      <div class="hero-callout">
        <strong>当前入口</strong>
        <span>最短路径：菜名输入 -> 结构化做法</span>
      </div>
    </div>
    <div class="hero-search-row">
      <input v-model="form.dishName" class="hero-search-input" placeholder="例如：红烧肉、宫保鸡丁、番茄炒蛋" />
      <button class="primary-button" type="button" @click="handleSubmit">生成做法</button>
    </div>
    <div class="form-grid">
      <label>
        <span>人数</span>
        <input v-model.number="form.servings" type="number" min="1" max="10" />
      </label>
      <label>
        <span>难度</span>
        <select v-model="form.difficulty">
          <option value="BEGINNER">新手</option>
          <option value="INTERMEDIATE">进阶</option>
          <option value="ADVANCED">熟练</option>
        </select>
      </label>
      <label>
        <span>口味</span>
        <input v-model="form.flavor" placeholder="例如：少辣、家常" />
      </label>
      <label class="full-width">
        <span>补充说明</span>
        <textarea
          v-model="form.additionalNote"
          rows="4"
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
  </section>
</template>
