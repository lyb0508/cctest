package com.example.foodai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 映射 application.yml 中 foodai.ai.* 配置。
 * <p>
 * provider 取值：
 * <ul>
 *   <li>stub   —— 本地示例生成器（不调用模型）</li>
 *   <li>ollama —— 调用本地 Ollama 服务（model-name 指定模型，如 qwen2.5:3b）</li>
 * </ul>
 * api-key 仅用于未来的云端模型接入，Ollama 本地服务无需密钥。
 */
@ConfigurationProperties(prefix = "foodai.ai")
public class AiProperties {

    /** 生成器选择：stub / ollama */
    private String provider = "stub";

    /** Ollama 模型名，如 qwen2.5:3b */
    private String modelName = "";

    /** 云端模型 API 密钥（Ollama 不需要） */
    private String apiKey = "";

    /** 模型服务地址，Ollama 默认 http://localhost:11434 */
    private String baseUrl = "";

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
