package com.example.foodai.ai.service;

import com.example.foodai.ai.model.AiDishGuideRequest;
import com.example.foodai.ai.model.AiRecipeResult;
import com.example.foodai.ai.prompt.DishGuidePromptTemplate;
import com.example.foodai.common.BusinessException;
import com.example.foodai.config.AiProperties;
import com.example.foodai.recipe.vo.RecipeStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过 langchain4j-ollama 调用本地 Ollama 服务生成菜谱（当前唯一真实 AI 实现）。
 * 调用流程：构造提示词 -> Ollama(format=json) 返回 JSON -> Jackson 解析为 AiRecipeResult
 *           -> 结构校验（失败自动重试一次）-> 步骤时长兜底归一化。
 * provider 非 "ollama" 时回退到 StubDishGuideGenerator。
 */
@Component
@Primary
public class OllamaDishGuideGenerator implements DishGuideGenerator {

    private static final Logger log = LoggerFactory.getLogger(OllamaDishGuideGenerator.class);

    private final AiProperties aiProperties;
    private final StubDishGuideGenerator fallbackGenerator;
    private final DishGuidePromptTemplate promptTemplate;
    private final ObjectMapper objectMapper;

    private volatile OllamaChatModel ollamaChatModel;

    public OllamaDishGuideGenerator(AiProperties aiProperties,
                                         StubDishGuideGenerator fallbackGenerator,
                                         DishGuidePromptTemplate promptTemplate,
                                         ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.fallbackGenerator = fallbackGenerator;
        this.promptTemplate = promptTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiRecipeResult generate(AiDishGuideRequest request) {
        // provider 开关：不是 ollama 就走本地示例，保证 stub 模式也能跑通全链路
        if (!"ollama".equalsIgnoreCase(aiProperties.getProvider())) {
            return fallbackGenerator.generate(request);
        }

        // 前置校验：配置缺失直接抛业务异常（500），避免带着空配置去调模型
        if (aiProperties.getBaseUrl() == null || aiProperties.getBaseUrl().isBlank()) {
            throw new BusinessException("AI provider is set to ollama but base-url is missing");
        }
        if (aiProperties.getModelName() == null || aiProperties.getModelName().isBlank()) {
            throw new BusinessException("AI provider is set to ollama but model-name is missing");
        }

        OllamaChatModel model = chatModel();

        // 第一次尝试：正常提示词
        AiRecipeResult result = tryGenerate(model, promptTemplate.render(request));
        if (!isValid(result)) {
            // 小模型偶尔会漏字段/写错字段名，用附加纠正指令重试一次再放弃
            log.warn("AI returned invalid recipe structure, retrying once. firstResult={}", result);
            result = tryGenerate(model, promptTemplate.render(request,
                    "Your previous answer was invalid: missing fields, wrong field names, or malformed JSON. "
                            + "Output ONLY valid JSON using exactly the field names listed above."));
        }
        if (!isValid(result)) {
            throw new BusinessException("AI returned an invalid recipe structure after retry");
        }
        return normalize(result);
    }

    /**
     * 调用模型并解析 JSON。任何异常（网络/超时/解析失败）都返回 null，
     * 由上层决定重试还是报错，避免把底层异常细节直接抛给客户端。
     */
    private AiRecipeResult tryGenerate(OllamaChatModel model, String prompt) {
        try {
            String json = model.chat(prompt);
            return objectMapper.readValue(extractJson(json), AiRecipeResult.class);
        } catch (Exception e) {
            log.warn("Failed to generate/parse AI recipe response: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 结构完整性校验：标题/描述/食材/步骤都非空，且每个步骤有标题和内容。
     * 防止模型输出"看起来是 JSON 但缺关键字段"的结果被直接展示。
     */
    private boolean isValid(AiRecipeResult result) {
        return result != null
                && result.title() != null && !result.title().isBlank()
                && result.description() != null && !result.description().isBlank()
                && result.ingredients() != null && !result.ingredients().isEmpty()
                && result.steps() != null && !result.steps().isEmpty()
                && result.steps().stream().allMatch(step -> step != null
                        && step.title() != null && !step.title().isBlank()
                        && step.content() != null && !step.content().isBlank());
    }

    /**
     * Fills in missing step durations by distributing totalTime evenly across steps,
     * so the frontend never renders an empty duration.
     */
    private AiRecipeResult normalize(AiRecipeResult result) {
        if (result.steps().isEmpty()) {
            return result;
        }
        // 前端固定渲染"预计 X 分钟"，模型可能漏给 duration；
        // 按 totalTime 均摊到每步作为兜底，最后一步吸收余数，保证时长总和≈totalTime
        int total = result.totalTime() == null ? 0 : result.totalTime();
        int base = total / result.steps().size();
        if (base < 1) {
            base = 5;
        }
        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < result.steps().size(); i++) {
            RecipeStep s = result.steps().get(i);
            Integer duration = s.duration();
            if (duration == null || duration <= 0) {
                duration = (i == result.steps().size() - 1 && total > 0)
                        ? Math.max(1, total - base * (result.steps().size() - 1))
                        : base;
            }
            steps.add(new RecipeStep(s.stepNo(), s.title(), s.content(), duration,
                    s.note() == null ? "" : s.note()));
        }
        return new AiRecipeResult(result.title(), result.description(), result.servings(), result.prepTime(),
                result.cookTime(), result.totalTime(), result.difficulty(), result.ingredients(), steps, result.tips());
    }

    /**
     * 懒加载单例：同一个 OllamaChatModel 复用连接配置；volatile + 双重检查保证线程安全。
     * 模型名/地址来自配置，改 application.yml 后重启即可生效。
     */
    private OllamaChatModel chatModel() {
        OllamaChatModel local = ollamaChatModel;
        if (local == null) {
            synchronized (this) {
                local = ollamaChatModel;
                if (local == null) {
                    local = OllamaChatModel.builder()
                            .baseUrl(aiProperties.getBaseUrl())
                            .modelName(aiProperties.getModelName())
                            .format("json")
                            .timeout(Duration.ofMinutes(3))
                            .build();
                    ollamaChatModel = local;
                }
            }
        }
        return local;
    }

    /**
     * 剥离模型偶尔会输出的 markdown 代码围栏（```json ... ```），只保留 JSON 本体。
     */
    private String extractJson(String text) {
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline >= 0 && lastFence > firstNewline) {
                trimmed = trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}
