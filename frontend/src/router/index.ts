import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import RecipeDetailView from "../views/RecipeDetailView.vue";

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
