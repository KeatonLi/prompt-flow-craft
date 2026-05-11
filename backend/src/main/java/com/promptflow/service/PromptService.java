package com.promptflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.client.llm.LLMClient;
import com.promptflow.dto.*;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.dto.llm.LLMResponse;
import com.promptflow.entity.AgentPrompt;
import com.promptflow.entity.SkillPrompt;
import com.promptflow.service.quality.PromptQualityService;
import com.promptflow.strategy.prompt.PromptStrategy;
import com.promptflow.strategy.prompt.PromptStrategyFactory;
import com.promptflow.strategy.prompt.StrategyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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
    private final PromptRecordService promptRecordService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PromptService(LLMClient llmClient,
                        PromptStrategyFactory strategyFactory,
                        PromptQualityService qualityService,
                        PromptRecordService promptRecordService) {
        this.llmClient = llmClient;
        this.strategyFactory = strategyFactory;
        this.qualityService = qualityService;
        this.promptRecordService = promptRecordService;
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

                        // 保存到历史记录
                        if (saveAfterComplete && fullContent.length() > 0) {
                            try {
                                saveGeneralPrompt(request.getTaskDescription(), fullContent.toString());
                            } catch (Exception e) {
                                logger.error("保存提示词失败", e);
                            }
                        }

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

    // ==================== Save Methods ====================

    /**
     * 保存通用提示词到 Agent 表
     */
    public AgentPrompt saveGeneralPrompt(String taskDescription, String generatedPrompt) {
        AgentPrompt agent = new AgentPrompt();
        agent.setName("通用提示词");
        agent.setRoleDescription(taskDescription);
        agent.setGeneratedPrompt(generatedPrompt);
        return promptRecordService.saveAgent(agent);
    }

    /**
     * 保存 Agent 提示词
     */
    public AgentPrompt saveAgentPrompt(String name, String roleDescription, String capabilities,
                                       String behaviors, String communicationStyle, String generatedPrompt) {
        AgentPrompt agent = new AgentPrompt();
        agent.setName(name);
        agent.setRoleDescription(roleDescription);
        agent.setCapabilities(capabilities);
        agent.setBehaviors(behaviors);
        agent.setCommunicationStyle(communicationStyle);
        agent.setGeneratedPrompt(generatedPrompt);
        return promptRecordService.saveAgent(agent);
    }

    /**
     * 保存 Skill 提示词
     */
    public SkillPrompt saveSkillPrompt(String name, String description, SkillPrompt.SkillType skillType,
                                       String method, String endpoint, String parameters,
                                       String outputDescription, String generatedPrompt) {
        SkillPrompt skill = new SkillPrompt();
        skill.setName(name);
        skill.setDescription(description);
        skill.setSkillType(skillType);
        skill.setMethod(method);
        skill.setEndpoint(endpoint);
        skill.setParameters(parameters);
        skill.setOutputDescription(outputDescription);
        skill.setGeneratedPrompt(generatedPrompt);
        return promptRecordService.saveSkill(skill);
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
        PromptRequest request = new PromptRequest();
        request.setTaskDescription(prompt);

        return generatePromptStream(request, onComplete, onError, false);
    }

    private String buildAgentPromptRequest(String name, String roleDescription, String capabilities,
                                         String behaviors, String communicationStyle) {
        StringBuilder sb = new StringBuilder();
        sb.append("请你为以下 AI Agent 生成一个专业、详细、结构完整的提示词。要求生成内容在 400-600 字之间。\n\n");
        sb.append("## Agent 基本信息\n\n");
        sb.append("**名称**: ").append(name).append("\n");
        sb.append("**角色定位**: ").append(roleDescription).append("\n\n");
        sb.append("## 核心能力\n").append(capabilities != null ? capabilities : "无").append("\n\n");
        sb.append("## 行为规范\n").append(behaviors != null ? behaviors : "无").append("\n\n");
        sb.append("## 对话风格\n").append(communicationStyle != null ? communicationStyle : "专业").append("\n\n");
        sb.append("## 输出要求\n\n");
        sb.append("1. **开头**: 使用一句精准的角色定义开篇，明确 Agent 的核心身份和价值\n");
        sb.append("2. **能力描述**: 详细列举 Agent 能做什么，使用什么方法/思路\n");
        sb.append("3. **行为准则**: 明确 Agent 在对话中应该遵循的原则和方式\n");
        sb.append("4. **沟通风格**: 说明 Agent 的表达特点和语气\n");
        sb.append("5. **输出格式**: 使用 Markdown 格式，适当使用列表、引用等结构化表达\n\n");
        sb.append("请用中文生成这段提示词，确保内容专业、实用、有深度，让 AI 能够准确理解并执行角色任务。");
        return sb.toString();
    }

    // ==================== Skill 生成方法 ====================

    /**
     * 生成 Skill 提示词文本（同步）
     */
    public String generateSkillPromptText(String name, String description, SkillPrompt.SkillType skillType,
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
    public SseEmitter generateSkillPromptStream(String name, String description, SkillPrompt.SkillType skillType,
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
        PromptRequest request = new PromptRequest();
        request.setTaskDescription(prompt);

        return generatePromptStream(request, onComplete, onError, false);
    }

    private String buildSkillPromptRequest(String name, String description, SkillPrompt.SkillType skillType,
                                         String method, String endpoint, String parameters,
                                         String outputDescription) {
        String skillTypeName = skillType != null ? skillType.name() : "api";
        String methodStr = method != null ? method : "GET";
        String endpointStr = endpoint != null ? endpoint : "/api/example";

        StringBuilder sb = new StringBuilder();
        sb.append("请你为以下 Skill 生成一个专业、详细、结构完整的提示词定义。要求生成内容在 400-600 字之间。\n\n");
        sb.append("## Skill 基本信息\n\n");
        sb.append("**名称**: ").append(name).append("\n");
        sb.append("**功能描述**: ").append(description).append("\n");
        sb.append("**类型**: ").append(skillTypeName).append("\n\n");
        sb.append("## 技术配置\n\n");
        sb.append("**请求方法**: ").append(methodStr).append("\n");
        sb.append("**接口地址**: ").append(endpointStr).append("\n\n");
        sb.append("## 输入参数\n");
        sb.append(parameters != null && !parameters.isEmpty() ? parameters : "未指定具体参数").append("\n\n");
        sb.append("## 输出描述\n");
        sb.append(outputDescription != null && !outputDescription.isEmpty() ? outputDescription : "未指定输出描述").append("\n\n");
        sb.append("## 输出要求\n\n");
        sb.append("1. **功能概述**: 用一句话精炼描述 Skill 的核心能力\n");
        sb.append("2. **使用场景**: 说明在什么情况下应该调用这个 Skill\n");
        sb.append("3. **参数说明**: 详细解释每个输入参数的作用、类型要求、是否必填\n");
        sb.append("4. **返回值处理**: 说明输出数据的结构、可能的结果状态\n");
        sb.append("5. **使用示例**: 提供 1-2 个典型的调用示例\n");
        sb.append("6. **注意事项**: 列出使用时的关键注意点和错误处理方式\n\n");
        sb.append("请用中文生成这段提示词，采用标准 Tool/Function Calling 格式，确保 AI 能够准确理解如何调用和使用这个 Skill。");
        return sb.toString();
    }
}