export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface DishGuideRequest {
  dishName: string;
  servings?: number;
  difficulty?: string;
  flavor?: string;
  additionalNote?: string;
}

export interface RecipeIngredient {
  name: string;
  amount: string;
}

export interface RecipeStep {
  stepNo: number;
  title: string;
  content: string;
  duration: number;
  note?: string;
}

export interface RecipeData {
  title: string;
  description: string;
  servings: number;
  prepTime: number;
  cookTime: number;
  totalTime: number;
  difficulty: string;
  ingredients: RecipeIngredient[];
  steps: RecipeStep[];
  tips: string[];
}

export interface RecipeDetailResponse {
  recipeId: number;
  generationType: string;
  title: string;
  summary: string;
  recipe: RecipeData;
}
