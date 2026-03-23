package com.promptflow.strategy.prompt.impl;

import com.promptflow.dto.PromptRequest;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.strategy.prompt.PromptStrategy;
import com.promptflow.strategy.prompt.StrategyContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("# 系统架构思维提示词工程师\n\n");
        promptBuilder.append("你是一位精通系统架构思维的资深AI提示词工程师。\n\n");
        
        promptBuilder.append("## 用户需求\n");
        promptBuilder.append("任务描述：").append(request.getTaskDescription()).append("\n");
        
        appendIfNotEmpty(promptBuilder, "目标受众", request.getTargetAudience());
        appendIfNotEmpty(promptBuilder, "输出格式", request.getOutputFormat());
        appendIfNotEmpty(promptBuilder, "语调风格", request.getTone());
        appendIfNotEmpty(promptBuilder, "内容长度", request.getLength());
        appendIfNotEmpty(promptBuilder, "约束条件", request.getConstraints());
        appendIfNotEmpty(promptBuilder, "参考示例", request.getExamples());
        
        promptBuilder.append("\n## 输出要求\n");
        promptBuilder.append("请按照系统架构思维的四层框架设计提示词系统：\n");
        promptBuilder.append("1. 核心定义层：角色建模、目标定义\n");
        promptBuilder.append("2. 交互接口层：输入规范、输出规格\n");
        promptBuilder.append("3. 内部处理层：能力拆解、流程设计\n");
        promptBuilder.append("4. 全局约束层：行为边界、安全规则\n\n");
        promptBuilder.append("请直接输出完整的提示词系统。");
        
        return promptBuilder.toString();
    }
    
    private void appendIfNotEmpty(StringBuilder builder, String label, String value) {
        if (value != null && !value.trim().isEmpty()) {
            builder.append(label).append("：").append(value).append("\n");
        }
    }
}