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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

    // 提示词模板缓存
    private String agentPromptTemplate;
    private String skillPromptTemplate;
    private String generatePromptTemplate;

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
        loadPromptTemplates();
    }

    /**
     * 启动时加载提示词模板
     */
    @PostConstruct
    public void loadPromptTemplates() {
        try {
            agentPromptTemplate = loadTemplate("prompts/agent_prompt_template.txt");
            skillPromptTemplate = loadTemplate("prompts/skill_prompt_template.txt");
            logger.info("提示词模板加载成功");
        } catch (Exception e) {
            logger.error("加载提示词模板失败", e);
            throw new RuntimeException("加载提示词模板失败", e);
        }
    }

    /**
     * 从 classpath 加载模板文件
     */
    private String loadTemplate(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("找不到模板文件: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取模板文件失败: " + path, e);
        }
    }

    /**
     * 替换模板中的占位符
     */
    private String replacePlaceholders(String template, Object... keyValues) {
        String result = template;
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = (String) keyValues[i];
            String value = keyValues[i + 1] != null ? (String) keyValues[i + 1] : "";
            result = result.replace("{" + key + "}", value);
        }
        return result;
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

        StringBuilder fullContent = new StringBuilder();
        StringBuilder pendingContent = new StringBuilder();
        AtomicBoolean foundPromptStart = new AtomicBoolean(false);
        AtomicInteger lastSentIndex = new AtomicInteger(0);

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

                llmClient.callStream(llmRequest,
                    chunk -> {
                        synchronized (fullContent) {
                            fullContent.append(chunk);
                        }
                        synchronized (pendingContent) {
                            pendingContent.append(chunk);

                            String pending = pendingContent.toString();

                            // 如果还没找到提示词开始，寻找 "# " 模式
                            if (!foundPromptStart.get()) {
                                // 查找 "\n# " 或开头的 "# " - 这表示真正的提示词内容开始了
                                int promptStart = -1;

                                // 先检查开头是否是 "# "
                                if (pending.length() >= 2 && pending.charAt(0) == '#' && pending.charAt(1) == ' ') {
                                    promptStart = 0;
                                } else {
                                    // 在字符串中查找 "\n# "
                                    for (int i = 0; i < pending.length() - 2; i++) {
                                        if (pending.charAt(i) == '\n' && pending.charAt(i+1) == '#' && pending.charAt(i+2) == ' ') {
                                            promptStart = i + 1;
                                            break;
                                        }
                                    }
                                }

                                if (promptStart >= 0) {
                                    foundPromptStart.set(true);
                                    String actualContent = pending.substring(promptStart);
                                    pendingContent.setLength(0);
                                    pendingContent.append(actualContent);
                                    lastSentIndex.set(actualContent.length());
                                    try {
                                        sendEvent(emitter, "message", actualContent);
                                    } catch (Exception e) {
                                        logger.error("发送SSE事件失败", e);
                                    }
                                }
                            } else {
                                // 已经找到开始了，发送新增的内容
                                String newContent = pendingContent.toString();
                                int sent = lastSentIndex.get();
                                if (newContent.length() > sent) {
                                    String toSend = newContent.substring(sent);
                                    lastSentIndex.set(newContent.length());
                                    if (!toSend.isEmpty()) {
                                        try {
                                            sendEvent(emitter, "message", toSend);
                                        } catch (Exception e) {
                                            logger.error("发送SSE事件失败", e);
                                        }
                                    }
                                }
                            }
                        }
                    },
                    () -> {
                        try {
                            sendEvent(emitter, "done", "{\"done\": true}");
                        } catch (Exception e) {
                            logger.error("发送完成事件失败", e);
                        }
                        emitter.complete();
                        if (onComplete != null) {
                            synchronized (fullContent) {
                                onComplete.accept(fullContent.toString());
                            }
                        }
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
        return replacePlaceholders(agentPromptTemplate,
            "name", name,
            "roleDescription", roleDescription,
            "capabilities", capabilities != null ? capabilities : "",
            "behaviors", behaviors != null ? behaviors : "",
            "communicationStyle", communicationStyle != null ? communicationStyle : "");
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
        return replacePlaceholders(skillPromptTemplate,
            "name", name,
            "description", description,
            "skillType", skillType != null ? skillType : "",
            "method", method != null ? method : "",
            "endpoint", endpoint != null ? endpoint : "",
            "parameters", parameters != null ? parameters : "",
            "outputDescription", outputDescription != null ? outputDescription : "");
    }
}