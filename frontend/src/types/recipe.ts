// 与后端 VO 一一对应的类型定义；字段变化时需同时改后端 record
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 生成菜谱请求体（可选字段与后端 @Size 限制保持一致）
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
