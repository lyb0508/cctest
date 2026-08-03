import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import RecipeDetailView from "../views/RecipeDetailView.vue";

// 路由表：首页（菜名输入）+ 菜谱详情页（:recipeId 动态参数，props 透传）
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView
    },
    {
      path: "/recipes/:recipeId",
      name: "recipe-detail",
      component: RecipeDetailView,
      props: true
    }
  ]
});

export default router;
