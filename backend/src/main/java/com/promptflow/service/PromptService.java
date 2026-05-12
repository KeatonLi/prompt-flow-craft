package com.promptflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.client.llm.LLMClient;
import com.promptflow.dto.*;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.dto.llm.LLMResponse;
import com.promptflow.service.quality.PromptQualityService;
import com.promptflow.strategy.prompt.PromptStrategy;
import com.promptflow.strategy.prompt.PromptStrategyFactory;
import com.promptflow.strategy.prompt.StrategyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 核心提示词服务
 * 协调 LLM 客户端、策略工厂和质量评估服务
 */
@Service
public class PromptService {

    private static final Logger logger = LoggerFactory.getLogger(PromptService.class);
    private static final long SSE_TIMEOUT = 300000L; // 5分钟超时

    private final LLMClient llmClient;
    private final PromptStrategyFactory strategyFactory;
    private final PromptQualityService qualityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.model}")
    private String defaultModel;

    @Value("${api.temperature:0.7}")
    private double temperature;

    @Value("${api.max-tokens:4000}")
    private int maxTokens;

    @Autowired
    public PromptService(LLMClient llmClient,
                        PromptStrategyFactory strategyFactory,
                        PromptQualityService qualityService) {
        this.llmClient = llmClient;
        this.strategyFactory = strategyFactory;
        this.qualityService = qualityService;
    }

    /**
     * 生成提示词（同步）
     */
    public String generatePrompt(PromptRequest request) {
        validateGenerateRequest(request);

        // 使用策略构建请求
        PromptStrategy strategy = strategyFactory.getStrategy("generate");
        StrategyContext context = StrategyContext.builder()
            .generateRequest(request)
            .streamMode(false)
            .build();

        LLMRequest llmRequest = strategy.buildRequest(context);

        // 调用 LLM
        LLMResponse response = llmClient.call(llmRequest);
        if (!response.isSuccess()) {
            throw new RuntimeException("生成失败: " + (response.getError() != null ? response.getError().getMessage() : "未知错误"));
        }

        return (String) strategy.parseResponse(response.getContent());
    }

    /**
     * 生成提示词（流式）
     * @return SseEmitter 用于流式输出
     */
    public SseEmitter generatePromptStream(PromptRequest request,
                                          Consumer<String> onComplete,
                                          Consumer<Throwable> onError) {
        return generatePromptStream(request, onComplete, onError, true);
    }

    /**
     * 生成提示词（流式）
     * @param saveAfterComplete 是否在完成后保存到历史记录
     * @return SseEmitter 用于流式输出
     */
    public SseEmitter generatePromptStream(PromptRequest request,
                                          Consumer<String> onComplete,
                                          Consumer<Throwable> onError,
                                          boolean saveAfterComplete) {
        validateGenerateRequest(request);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                // 使用策略构建请求
                PromptStrategy strategy = strategyFactory.getStrategy("generate");
                StrategyContext context = StrategyContext.builder()
                    .generateRequest(request)
                    .streamMode(true)
                    .build();

                LLMRequest llmRequest = strategy.buildRequest(context);

                // 流式调用
                StringBuilder fullContent = new StringBuilder();

                llmClient.callStream(llmRequest,
                    chunk -> {
                        // 发送内容片段
                        fullContent.append(chunk);
                        sendEvent(emitter, "message", chunk);
                    },
                    () -> {
                        // 完成
                        sendEvent(emitter, "done", "{\"done\": true}");
                        emitter.complete();

                        if (onComplete != null) onComplete.accept(fullContent.toString());
                    },
                    error -> {
                        // 错误
                        handleStreamError(emitter, error, onError);
                    }
                );

            } catch (Exception e) {
                handleStreamError(emitter, e, onError);
            }
        });

        emitter.onCompletion(executor::shutdown);
        emitter.onTimeout(executor::shutdown);
        emitter.onError(e -> executor.shutdown());

        return emitter;
    }
    
    /**
     * 优化提示词
     */
    public OptimizeResponse optimizePrompt(OptimizeRequest request) {
        validateOptimizeRequest(request);
        
        String optimizationType = request.getOptimizationType();
        if (optimizationType == null || optimizationType.isEmpty()) {
            optimizationType = "general";
        }
        
        String targetModel = request.getTargetModel();
        if (targetModel == null || targetModel.isEmpty()) {
            targetModel = "general";
        }
        
        // 评估原始提示词质量
        int scoreBefore = qualityService.evaluate(request.getPrompt());
        
        // 使用策略进行优化
        PromptStrategy strategy = strategyFactory.getStrategy(optimizationType);
        StrategyContext context = StrategyContext.builder()
            .optimizeRequest(request)
            .originalPrompt(request.getPrompt())
            .optimizationType(optimizationType)
            .targetModel(targetModel)
            .build();
        
        LLMRequest llmRequest = strategy.buildRequest(context);
        
        // 调用 LLM
        LLMResponse llmResponse = llmClient.call(llmRequest);
        if (!llmResponse.isSuccess()) {
            throw new RuntimeException("优化失败: " + (llmResponse.getError() != null ? llmResponse.getError().getMessage() : "未知错误"));
        }
        
        // 解析响应
        OptimizeResponse response = (OptimizeResponse) strategy.parseResponse(llmResponse.getContent());
        
        // 评估优化后的质量
        int scoreAfter = qualityService.evaluate(response.getOptimizedPrompt());
        response.setScoreBefore(scoreBefore);
        response.setScoreAfter(scoreAfter);
        
        return response;
    }
    
    /**
     * 分析提示词质量
     */
    public AnalyzeResponse analyzePrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("提示词内容不能为空");
        }
        return qualityService.analyze(prompt);
    }
    
    // ========== 私有辅助方法 ==========
    
    private void validateGenerateRequest(PromptRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求不能为空");
        }
        if (request.getTaskDescription() == null || request.getTaskDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("任务描述不能为空");
        }
    }
    
    private void validateOptimizeRequest(OptimizeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求不能为空");
        }
        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            throw new IllegalArgumentException("提示词内容不能为空");
        }
    }

    private void sendEvent(SseEmitter emitter, String event, String data) {
        try {
            emitter.send(SseEmitter.event()
                .name(event)
                .data(data));
        } catch (IOException e) {
            logger.error("发送SSE事件失败", e);
        }
    }
    
    private void handleStreamError(SseEmitter emitter, Throwable error, Consumer<Throwable> onError) {
        logger.error("流式处理失败", error);
        try {
            sendEvent(emitter, "error", "{\"error\": \"" + error.getMessage() + "\"}");
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        if (onError != null) onError.accept(error);
    }

    // ==================== Agent 生成方法 ====================

    /**
     * 生成 Agent 提示词文本（同步）
     */
    public String generateAgentPromptText(String name, String roleDescription, String capabilities,
                                        String behaviors, String communicationStyle) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent名称不能为空");
        }
        if (roleDescription == null || roleDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent角色定位不能为空");
        }

        String prompt = buildAgentPromptRequest(name, roleDescription, capabilities, behaviors, communicationStyle);
        PromptRequest request = new PromptRequest();
        request.setTaskDescription(prompt);
        return generatePrompt(request);
    }

    /**
     * 生成 Agent 提示词（流式）
     * 直接调用 LLM，绕过策略模式的二次包装
     */
    public SseEmitter generateAgentPromptStream(String name, String roleDescription, String capabilities,
                                               String behaviors, String communicationStyle,
                                               Consumer<String> onComplete,
                                               Consumer<String> onChunk,
                                               Consumer<Throwable> onError) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent名称不能为空");
        }
        if (roleDescription == null || roleDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent角色定位不能为空");
        }

        String prompt = buildAgentPromptRequest(name, roleDescription, capabilities, behaviors, communicationStyle);
        return directStreamCall(prompt, onComplete, onError);
    }

    /**
     * 直接流式调用 LLM，绕过策略模式
     */
    private SseEmitter directStreamCall(String prompt, Consumer<String> onComplete, Consumer<Throwable> onError) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                // 直接构建 LLM 请求，不走策略模式
                LLMRequest llmRequest = LLMRequest.builder()
                    .model(defaultModel)
                    .messages(List.of(
                        LLMRequest.Message.user(prompt)
                    ))
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .stream(true)
                    .build();

                StringBuilder fullContent = new StringBuilder();

                llmClient.callStream(llmRequest,
                    chunk -> {
                        fullContent.append(chunk);
                        // 直接发送 chunk，不做处理
                        sendEvent(emitter, "message", chunk);
                    },
                    () -> {
                        sendEvent(emitter, "done", "{\"done\": true}");
                        emitter.complete();
                        if (onComplete != null) onComplete.accept(fullContent.toString());
                    },
                    error -> {
                        handleStreamError(emitter, error, onError);
                    }
                );

            } catch (Exception e) {
                handleStreamError(emitter, e, onError);
            }
        });

        emitter.onCompletion(executor::shutdown);
        emitter.onTimeout(executor::shutdown);
        emitter.onError(e -> executor.shutdown());

        return emitter;
    }

    private String buildAgentPromptRequest(String name, String roleDescription, String capabilities,
                                         String behaviors, String communicationStyle) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个专业的 AI 提示词工程师。请为以下 Agent 生成提示词，输出必须使用 Markdown 格式。\n\n");
        sb.append("## Agent 信息\n\n");
        sb.append("**名称**：").append(name).append("\n");
        sb.append("**角色定位**：").append(roleDescription).append("\n");
        if (capabilities != null && !capabilities.isEmpty()) {
            sb.append("**核心能力**：\n").append(capabilities).append("\n");
        }
        if (behaviors != null && !behaviors.isEmpty()) {
            sb.append("**行为准则**：\n").append(behaviors).append("\n");
        }
        if (communicationStyle != null && !communicationStyle.isEmpty()) {
            sb.append("**对话风格**：").append(communicationStyle).append("\n");
        }
        sb.append("\n---\n\n");
        sb.append("请直接输出完整的提示词内容，使用 Markdown 格式（包含标题、列表、代码块等），无需任何解释。");
        return sb.toString();
    }

    // ==================== Skill 生成方法 ====================

    /**
     * 生成 Skill 提示词文本（同步）
     */
    public String generateSkillPromptText(String name, String description, String skillType,
                                        String method, String endpoint, String parameters,
                                        String outputDescription) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill名称不能为空");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill功能描述不能为空");
        }

        String prompt = buildSkillPromptRequest(name, description, skillType, method, endpoint, parameters, outputDescription);
        PromptRequest request = new PromptRequest();
        request.setTaskDescription(prompt);
        return generatePrompt(request);
    }

    /**
     * 生成 Skill 提示词（流式）
     */
    public SseEmitter generateSkillPromptStream(String name, String description, String skillType,
                                               String method, String endpoint, String parameters,
                                               String outputDescription,
                                               Consumer<String> onComplete,
                                               Consumer<String> onChunk,
                                               Consumer<Throwable> onError) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill名称不能为空");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill功能描述不能为空");
        }

        String prompt = buildSkillPromptRequest(name, description, skillType, method, endpoint, parameters, outputDescription);
        return directStreamCall(prompt, onComplete, onError);
    }

    private String buildSkillPromptRequest(String name, String description, String skillType,
                                         String method, String endpoint, String parameters,
                                         String outputDescription) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个专业的 AI 提示词工程师。请为以下 Skill 生成提示词定义，输出必须使用 Markdown 格式。\n\n");
        sb.append("## Skill 信息\n\n");
        sb.append("**名称**：").append(name).append("\n");
        sb.append("**功能描述**：").append(description).append("\n");
        if (skillType != null) {
            sb.append("**类型**：").append(skillType).append("\n");
        }
        if (method != null && !method.isEmpty()) {
            sb.append("**请求方法**：").append(method).append("\n");
        }
        if (endpoint != null && !endpoint.isEmpty()) {
            sb.append("**接口端点**：").append(endpoint).append("\n");
        }
        if (parameters != null && !parameters.isEmpty()) {
            sb.append("**输入参数**：\n").append(parameters).append("\n");
        }
        if (outputDescription != null && !outputDescription.isEmpty()) {
            sb.append("**输出格式**：").append(outputDescription).append("\n");
        }
        sb.append("\n---\n\n");
        sb.append("请直接输出完整的提示词内容，使用 Markdown 格式（包含标题、列表、代码块等），采用标准 Tool/Function Calling 格式，无需任何解释。");
        return sb.toString();
    }
}