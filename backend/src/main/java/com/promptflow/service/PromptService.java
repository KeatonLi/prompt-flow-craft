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
    private final PromptCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public PromptService(LLMClient llmClient,
                        PromptStrategyFactory strategyFactory,
                        PromptQualityService qualityService,
                        PromptCacheService cacheService) {
        this.llmClient = llmClient;
        this.strategyFactory = strategyFactory;
        this.qualityService = qualityService;
        this.cacheService = cacheService;
    }
    
    /**
     * 生成提示词（同步）
     */
    public String generatePrompt(PromptRequest request) {
        validateGenerateRequest(request);
        
        // 检查缓存
        String cached = cacheService.getCachedPrompt(request);
        if (cached != null) {
            logger.info("缓存命中，直接返回");
            return cached;
        }
        
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
        validateGenerateRequest(request);
        
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        executor.execute(() -> {
            try {
                // 检查缓存
                String cached = cacheService.getCachedPrompt(request);
                if (cached != null) {
                    streamFromCache(emitter, cached);
                    if (onComplete != null) onComplete.accept(cached);
                    return;
                }
                
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
    
    private void streamFromCache(SseEmitter emitter, String cached) throws Exception {
        // 模拟流式效果，逐段发送
        for (int i = 0; i < cached.length(); i += 5) {
            String chunk = cached.substring(i, Math.min(i + 5, cached.length()));
            sendEvent(emitter, "message", chunk);
            Thread.sleep(10);
        }
        sendEvent(emitter, "done", "{\"done\": true}");
        emitter.complete();
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
}