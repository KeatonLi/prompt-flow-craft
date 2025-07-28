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
        
        // 角色定义和专业背景
        promptBuilder.append("# 角色定义\n");
        promptBuilder.append("你是一位资深的AI提示词工程师，拥有丰富的人工智能交互设计经验。你深谙各种AI模型的特点，");
        promptBuilder.append("擅长设计高效、精准的提示词，能够最大化发挥AI的能力。\n\n");
        
        // 任务目标
        promptBuilder.append("# 任务目标\n");
        promptBuilder.append("根据用户提供的需求信息，设计一个结构化、可执行的AI提示词。这个提示词应该能够：\n");
        promptBuilder.append("- 准确传达用户的真实意图\n");
        promptBuilder.append("- 引导AI产生高质量、符合预期的输出\n");
        promptBuilder.append("- 具备良好的可重复性和稳定性\n\n");
        
        // 用户需求信息
        promptBuilder.append("# 用户需求信息\n");
        
        if (request.getTaskDescription() != null && !request.getTaskDescription().trim().isEmpty()) {
            promptBuilder.append("**核心任务：** ").append(request.getTaskDescription()).append("\n");
        }
        
        if (request.getTargetAudience() != null && !request.getTargetAudience().trim().isEmpty()) {
            promptBuilder.append("**目标用户：** ").append(request.getTargetAudience()).append("\n");
        }
        
        if (request.getOutputFormat() != null && !request.getOutputFormat().trim().isEmpty()) {
            promptBuilder.append("**期望格式：** ").append(request.getOutputFormat()).append("\n");
        }
        
        if (request.getConstraints() != null && !request.getConstraints().trim().isEmpty()) {
            promptBuilder.append("**限制条件：** ").append(request.getConstraints()).append("\n");
        }
        
        if (request.getExamples() != null && !request.getExamples().trim().isEmpty()) {
            promptBuilder.append("**参考案例：** ").append(request.getExamples()).append("\n");
        }
        
        if (request.getTone() != null && !request.getTone().trim().isEmpty()) {
            promptBuilder.append("**语言风格：** ").append(request.getTone()).append("\n");
        }
        
        if (request.getLength() != null && !request.getLength().trim().isEmpty()) {
            promptBuilder.append("**内容篇幅：** ").append(request.getLength()).append("\n");
        }
        
        // 设计要求和最佳实践
        promptBuilder.append("\n# 提示词设计要求\n");
        promptBuilder.append("请按照以下结构和原则设计提示词：\n\n");
        
        promptBuilder.append("## 1. 角色设定（必需）\n");
        promptBuilder.append("- 为AI定义一个具体、专业的角色身份\n");
        promptBuilder.append("- 说明该角色的专业背景和能力特长\n");
        promptBuilder.append("- 确保角色与任务需求高度匹配\n\n");
        
        promptBuilder.append("## 2. 任务描述（必需）\n");
        promptBuilder.append("- 清晰描述AI需要完成的具体任务\n");
        promptBuilder.append("- 分解复杂任务为可执行的步骤\n");
        promptBuilder.append("- 明确任务的核心目标和成功标准\n\n");
        
        promptBuilder.append("## 3. 输出规范（必需）\n");
        promptBuilder.append("- 详细说明期望的输出格式和结构\n");
        promptBuilder.append("- 指定内容的组织方式和呈现形式\n");
        promptBuilder.append("- 如有必要，提供输出模板或示例\n\n");
        
        promptBuilder.append("## 4. 约束条件（根据需要）\n");
        promptBuilder.append("- 明确列出必须遵守的限制和要求\n");
        promptBuilder.append("- 说明不可接受的内容或行为\n");
        promptBuilder.append("- 设定质量标准和评判criteria\n\n");
        
        promptBuilder.append("## 5. 示例引导（推荐）\n");
        promptBuilder.append("- 提供1-2个高质量的输入输出示例\n");
        promptBuilder.append("- 通过示例展示期望的思考过程\n");
        promptBuilder.append("- 帮助AI理解任务的具体执行方式\n\n");
        
        // 质量标准
        promptBuilder.append("# 质量标准\n");
        promptBuilder.append("生成的提示词必须满足：\n");
        promptBuilder.append("✓ **清晰性**：语言简洁明了，避免歧义\n");
        promptBuilder.append("✓ **完整性**：包含所有必要的指导信息\n");
        promptBuilder.append("✓ **可操作性**：AI能够直接理解并执行\n");
        promptBuilder.append("✓ **针对性**：高度契合用户的具体需求\n");
        promptBuilder.append("✓ **专业性**：体现相关领域的专业知识\n\n");
        
        // 输出指令
        promptBuilder.append("# 输出指令\n");
        promptBuilder.append("请直接输出完整的AI提示词，无需添加任何解释、分析或元信息。");
        promptBuilder.append("确保提示词可以直接复制使用，具备良好的实用性。");
        
        return promptBuilder.toString();
    }
}