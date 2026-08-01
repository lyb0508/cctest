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
        if (!"ollama".equalsIgnoreCase(aiProperties.getProvider())) {
            return fallbackGenerator.generate(request);
        }

        if (aiProperties.getBaseUrl() == null || aiProperties.getBaseUrl().isBlank()) {
            throw new BusinessException("AI provider is set to ollama but base-url is missing");
        }
        if (aiProperties.getModelName() == null || aiProperties.getModelName().isBlank()) {
            throw new BusinessException("AI provider is set to ollama but model-name is missing");
        }

        OllamaChatModel model = chatModel();

        AiRecipeResult result = tryGenerate(model, promptTemplate.render(request));
        if (!isValid(result)) {
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

    private AiRecipeResult tryGenerate(OllamaChatModel model, String prompt) {
        try {
            String json = model.chat(prompt);
            return objectMapper.readValue(extractJson(json), AiRecipeResult.class);
        } catch (Exception e) {
            log.warn("Failed to generate/parse AI recipe response: {}", e.getMessage());
            return null;
        }
    }

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