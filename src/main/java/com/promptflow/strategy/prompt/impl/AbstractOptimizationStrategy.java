package com.promptflow.strategy.prompt.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.OptimizeResponse;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.strategy.prompt.PromptStrategy;
import com.promptflow.strategy.prompt.StrategyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 优化策略抽象基类
 * 提供通用的优化逻辑和响应解析
 */
public abstract class AbstractOptimizationStrategy implements PromptStrategy {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${api.model}")
    protected String defaultModel;
    
    @Value("${prompt.optimization.temperature:0.7}")
    protected double optimizationTemperature;
    
    @Value("${prompt.optimization.max-tokens:4000}")
    protected int optimizationMaxTokens;
    
    /**
     * 获取优化类型描述
     */
    protected abstract String getOptimizationTypeDescription();
    
    /**
     * 获取目标模型描述
     */
    protected String getTargetModelDescription(String targetModel) {
        return switch (targetModel != null ? targetModel : "general") {
            case "gpt" -> "针对GPT系列模型优化\n";
            case "claude" -> "针对Claude模型优化\n";
            default -> "通用优化，适用于各种LLM\n";
        };
    }
    
    @Override
    public LLMRequest buildRequest(StrategyContext context) {
        String userPrompt = context.getOriginalPrompt();
        String targetModel = context.getTargetModel();
        
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            throw new IllegalArgumentException("原始提示词不能为空");
        }
        
        String systemPrompt = buildOptimizePrompt(userPrompt, targetModel);
        
        return LLMRequest.builder()
            .model(defaultModel)
            .messages(List.of(
                LLMRequest.Message.user(systemPrompt)
            ))
            .temperature(optimizationTemperature)
            .maxTokens(optimizationMaxTokens)
            .stream(false)
            .build();
    }
    
    @Override
    public Object parseResponse(String content) {
        try {
            return parseOptimizeResponse(content);
        } catch (Exception e) {
            logger.warn("解析优化结果失败，返回原始内容", e);
            return createFallbackResponse(content);
        }
    }
    
    /**
     * 构建优化提示词
     */
    protected String buildOptimizePrompt(String userPrompt, String targetModel) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("你是一位专业的AI提示词优化专家。你的任务是根据用户的需求优化提示词。\n\n");
        
        promptBuilder.append("## 原始提示词\n");
        promptBuilder.append(userPrompt).append("\n\n");
        
        promptBuilder.append("## 优化重点\n");
        promptBuilder.append(getOptimizationTypeDescription()).append("\n");
        
        promptBuilder.append("## 目标模型\n");
        promptBuilder.append(getTargetModelDescription(targetModel)).append("\n");
        
        promptBuilder.append("## 输出要求\n");
        promptBuilder.append("请按以下JSON格式返回优化结果：\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"optimizedPrompt\": \"优化后的完整提示词\",\n");
        promptBuilder.append("  \"explanation\": \"优化说明，解释你做了哪些改进及其原因\",\n");
        promptBuilder.append("  \"improvements\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"type\": \"改进类型\",\n");
        promptBuilder.append("      \"description\": \"发现的问题\",\n");
        promptBuilder.append("      \"suggestion\": \"改进建议\"\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}\n\n");
        promptBuilder.append("请确保返回的是有效的JSON格式。");
        
        return promptBuilder.toString();
    }
    
    /**
     * 解析优化响应
     */
    protected OptimizeResponse parseOptimizeResponse(String content) {
        try {
            // 提取 JSON 部分
            Pattern jsonPattern = Pattern.compile("\\{[\\s\\S]*\\}", Pattern.MULTILINE);
            Matcher matcher = jsonPattern.matcher(content);
            
            String jsonStr = content;
            if (matcher.find()) {
                jsonStr = matcher.group();
            }
            
            Map<String, Object> result = objectMapper.readValue(jsonStr, Map.class);
            
            OptimizeResponse response = new OptimizeResponse();
            response.setOptimizedPrompt((String) result.get("optimizedPrompt"));
            response.setExplanation((String) result.get("explanation"));
            
            // 解析 improvements
            List<Map<String, String>> improvementsList = (List<Map<String, String>>) result.get("improvements");
            if (improvementsList != null) {
                List<OptimizeResponse.Improvement> improvements = new ArrayList<>();
                for (Map<String, String> imp : improvementsList) {
                    improvements.add(new OptimizeResponse.Improvement(
                        imp.get("type"),
                        imp.get("description"),
                        imp.get("suggestion")
                    ));
                }
                response.setImprovements(improvements);
            }
            
            return response;
            
        } catch (Exception e) {
            logger.warn("解析JSON响应失败", e);
            return createFallbackResponse(content);
        }
    }
    
    /**
     * 创建回退响应
     */
    protected OptimizeResponse createFallbackResponse(String content) {
        OptimizeResponse response = new OptimizeResponse();
        response.setOptimizedPrompt(content);
        response.setExplanation("优化完成，但解析详细结果时出现问题");
        response.setImprovements(new ArrayList<>());
        return response;
    }
}