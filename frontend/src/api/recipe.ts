import http from "./http";
import type { ApiResponse, DishGuideRequest, RecipeDetailResponse } from "../types/recipe";

// 生成菜谱：成功返回 data.data（code 已由 http 拦截器校验为非 0 才会走到这里）
export async function createDishGuide(payload: DishGuideRequest): Promise<RecipeDetailResponse> {
  const response = await http.post<ApiResponse<RecipeDetailResponse>>("/recipes/dish-guide", payload);
  return response.data.data;
}

// 查询菜谱详情：路由参数 recipeId 可能为字符串，透传给后端
export async function getRecipeDetail(recipeId: string): Promise<RecipeDetailResponse> {
  const response = await http.get<ApiResponse<RecipeDetailResponse>>(`/recipes/${recipeId}`);
  return response.data.data;
}
