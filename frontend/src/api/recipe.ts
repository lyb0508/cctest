import http from "./http";
import type { ApiResponse, DishGuideRequest, RecipeDetailResponse } from "../types/recipe";

export async function createDishGuide(payload: DishGuideRequest): Promise<RecipeDetailResponse> {
  const response = await http.post<ApiResponse<RecipeDetailResponse>>("/recipes/dish-guide", payload);
  return response.data.data;
}

export async function getRecipeDetail(recipeId: string): Promise<RecipeDetailResponse> {
  const response = await http.get<ApiResponse<RecipeDetailResponse>>(`/recipes/${recipeId}`);
  return response.data.data;
}
