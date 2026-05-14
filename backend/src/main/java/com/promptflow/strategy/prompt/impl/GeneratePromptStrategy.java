package com.promptflow.strategy.prompt.impl;

import com.promptflow.dto.PromptRequest;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.strategy.prompt.PromptStrategy;
import com.promptflow.strategy.prompt.StrategyContext;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 提示词生成策略
 * 基于用户需求生成新的提示词
 */
@Component
public class GeneratePromptStrategy implements PromptStrategy {

    @Value("${api.model}")
    private String defaultModel;

    @Value("${api.temperature:0.7}")
    private double defaultTemperature;

    @Value("${api.max-tokens:4000}")
    private int defaultMaxTokens;

    private String generatePromptTemplate;

    @PostConstruct
    public void init() {
        loadPromptTemplate();
    }

    private void loadPromptTemplate() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("prompts/generate_prompt_template.txt")) {
            if (is == null) {
                throw new RuntimeException("找不到模板文件: prompts/generate_prompt_template.txt");
            }
            generatePromptTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取模板文件失败", e);
        }
    }

    private String replacePlaceholders(String template, Object... keyValues) {
        String result = template;
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = (String) keyValues[i];
            String value = keyValues[i + 1] != null ? (String) keyValues[i + 1] : "";
            result = result.replace("{" + key + "}", value);
        }
        return result;
    }

    @Override
    public String getStrategyType() {
        return "generate";
    }

    @Override
    public String getStrategyName() {
        return "提示词生成";
    }

    @Override
    public String getStrategyDescription() {
        return "根据用户需求和参数生成专业的AI提示词";
    }

    @Override
    public LLMRequest buildRequest(StrategyContext context) {
        PromptRequest request = context.getGenerateRequest();
        if (request == null) {
            throw new IllegalArgumentException("生成请求不能为空");
        }

        String systemPrompt = buildSystemPrompt(request);

        return LLMRequest.builder()
            .model(defaultModel)
            .messages(List.of(
                LLMRequest.Message.user(systemPrompt)
            ))
            .temperature(defaultTemperature)
            .maxTokens(defaultMaxTokens)
            .stream(context.isStreamMode())
            .build();
    }

    @Override
    public Object parseResponse(String content) {
        // 生成策略直接返回文本内容
        return content;
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(PromptRequest request) {
        return replacePlaceholders(generatePromptTemplate,
            "taskDescription", request.getTaskDescription() != null ? request.getTaskDescription() : "",
            "targetAudience", request.getTargetAudience() != null ? request.getTargetAudience() : "",
            "outputFormat", request.getOutputFormat() != null ? request.getOutputFormat() : "",
            "tone", request.getTone() != null ? request.getTone() : "",
            "length", request.getLength() != null ? request.getLength() : "",
            "constraints", request.getConstraints() != null ? request.getConstraints() : "",
            "examples", request.getExamples() != null ? request.getExamples() : "");
    }
}