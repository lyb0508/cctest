package com.example.foodai.ai.service;

import com.example.foodai.ai.model.AiDishGuideRequest;
import com.example.foodai.ai.model.AiRecipeResult;
import com.example.foodai.ai.prompt.DishGuidePromptTemplate;
import com.example.foodai.recipe.vo.RecipeIngredient;
import com.example.foodai.recipe.vo.RecipeStep;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 本地示例生成器：provider=stub 时（或 Ollama 未启用时）返回固定模板数据，
 * 用于不依赖模型即可演示/开发前端。promptTemplate 仅用于在 tips 里展示提示词预览。
 */
@Component
public class StubDishGuideGenerator implements DishGuideGenerator {

    private final DishGuidePromptTemplate promptTemplate;

    public StubDishGuideGenerator(DishGuidePromptTemplate promptTemplate) {
        this.promptTemplate = promptTemplate;
    }

    @Override
    public AiRecipeResult generate(AiDishGuideRequest request) {
        String promptPreview = promptTemplate.render(request);
        String dishName = request.dishName();
        int servings = request.servings() == null ? 2 : request.servings();
        String difficulty = request.difficulty() == null || request.difficulty().isBlank()
                ? "BEGINNER"
                : request.difficulty();
        String flavor = request.flavor() == null || request.flavor().isBlank()
                ? "家常"
                : request.flavor();

        return new AiRecipeResult(
                dishName + "做法",
                "这是一份适合" + servings + "人食用的" + flavor + "风味示例做法，当前由本地结构化生成器返回，后续可替换成 Ollama 本地模型的真实输出。",
                servings,
                10,
                18,
                28,
                difficulty,
                List.of(
                        new RecipeIngredient(dishName, servings + "人份主料"),
                        new RecipeIngredient("蒜末", "1 勺"),
                        new RecipeIngredient("生抽", "2 勺"),
                        new RecipeIngredient("食用油", "适量")
                ),
                List.of(
                        new RecipeStep(1, "准备食材", "处理主食材，准备好调味料，确认锅具和案板都已就位。", 6, "主食材尽量切成均匀大小"),
                        new RecipeStep(2, "开始烹饪", "热锅下油，先处理香料，再放入主食材翻炒或炖煮至成熟。", 10, "火力保持中火，避免外焦里生"),
                        new RecipeStep(3, "调味收尾", "加入调味料，试味后收汁或装盘即可。", 4, "出锅前先少量试味")
                ),
                List.of(
                        "如果你是新手，建议把食材和调味料提前准备好再开火。",
                        "当前返回的是本地示例结构，后续接入真实模型后会生成更贴合菜名的做法。",
                        "当前 Prompt 预览长度：" + promptPreview.length()
                )
        );
    }
}
