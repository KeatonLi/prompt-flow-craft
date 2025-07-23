package com.promptflow.service;

import com.promptflow.dto.PromptRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class PromptGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptGenerationService.class);
    
    private final WebClient webClient;
    
    @Autowired
    private PromptCacheService promptCacheService;
    
    @Value("${qwen.api.key}")
    private String apiKey;
    
    @Value("${qwen.api.base-url}")
    private String baseUrl;
    
    @Value("${qwen.api.model}")
    private String model;
    
    @Value("${qwen.api.temperature}")
    private double temperature;
    
    @Value("${qwen.api.max-tokens}")
    private int maxTokens;
    
    public PromptGenerationService() {
        this.webClient = WebClient.builder().build();
    }
    
    public String generatePrompt(PromptRequest request) {
        try {
            // 首先检查缓存
            String cachedPrompt = promptCacheService.getCachedPrompt(request);
            if (cachedPrompt != null) {
                logger.info("从缓存返回结果，避免API调用");
                return cachedPrompt;
            }
            
            logger.info("缓存未命中，调用API生成新的提示词");
            String systemPrompt = buildSystemPrompt(request);
            logger.debug("Generated system prompt: {}", systemPrompt);
            
            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", systemPrompt)
                ),
                "temperature", temperature,
                "max_tokens", maxTokens
            );
            
            // 调用Qwen API
            Mono<Map> responseMono = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);
            
            Map<String, Object> response = responseMono.block();
            
            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String generatedPrompt = (String) message.get("content");
                    
                    logger.debug("AI generated prompt: {}", generatedPrompt);
                    
                    // 保存到缓存
                    promptCacheService.saveToCache(request, generatedPrompt);
                    
                    return generatedPrompt;
                }
            }
            
            throw new RuntimeException("API响应格式错误");
            
        } catch (Exception e) {
            logger.error("Error generating prompt", e);
            throw new RuntimeException("生成提示词时发生错误: " + e.getMessage(), e);
        }
    }
    
    private String buildSystemPrompt(PromptRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("你是一个专业的AI提示词工程师，请根据用户的需求生成一个高质量的提示词。\n\n");
        
        promptBuilder.append("用户需求信息：\n");
        
        if (request.getTaskDescription() != null && !request.getTaskDescription().trim().isEmpty()) {
            promptBuilder.append("任务描述：").append(request.getTaskDescription()).append("\n");
        }
        
        if (request.getTargetAudience() != null && !request.getTargetAudience().trim().isEmpty()) {
            promptBuilder.append("目标受众：").append(request.getTargetAudience()).append("\n");
        }
        
        if (request.getOutputFormat() != null && !request.getOutputFormat().trim().isEmpty()) {
            promptBuilder.append("输出格式：").append(request.getOutputFormat()).append("\n");
        }
        
        if (request.getConstraints() != null && !request.getConstraints().trim().isEmpty()) {
            promptBuilder.append("约束条件：").append(request.getConstraints()).append("\n");
        }
        
        if (request.getExamples() != null && !request.getExamples().trim().isEmpty()) {
            promptBuilder.append("参考示例：").append(request.getExamples()).append("\n");
        }
        
        if (request.getTone() != null && !request.getTone().trim().isEmpty()) {
            promptBuilder.append("语调风格：").append(request.getTone()).append("\n");
        }
        
        if (request.getLength() != null && !request.getLength().trim().isEmpty()) {
            promptBuilder.append("内容长度：").append(request.getLength()).append("\n");
        }
        
        promptBuilder.append("\n请基于以上信息，生成一个结构清晰、逻辑严密的AI提示词。提示词应该：\n");
        promptBuilder.append("1. 包含明确的角色定义\n");
        promptBuilder.append("2. 详细的任务说明\n");
        promptBuilder.append("3. 具体的输出要求\n");
        promptBuilder.append("4. 必要的约束条件\n");
        promptBuilder.append("5. 适当的示例说明（如果需要）\n\n");
        promptBuilder.append("请直接输出生成的提示词，不需要额外的解释。");
        
        return promptBuilder.toString();
    }
}