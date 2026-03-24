package com.promptflow.strategy.prompt;

import com.promptflow.dto.OptimizeRequest;
import com.promptflow.dto.PromptRequest;

import java.util.Map;

/**
 * 策略上下文
 * 封装策略执行所需的所有信息
 */
public class StrategyContext {
    
    // 生成请求
    private PromptRequest generateRequest;
    
    // 优化请求
    private OptimizeRequest optimizeRequest;
    
    // 原始提示词（用于优化场景）
    private String originalPrompt;
    
    // 优化类型
    private String optimizationType;
    
    // 目标模型
    private String targetModel;
    
    // 额外参数
    private Map<String, Object> extraParams;
    
    // 流式模式
    private boolean streamMode = false;
    
    // 私有构造函数，使用 Builder 创建
    private StrategyContext() {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters
    public PromptRequest getGenerateRequest() { return generateRequest; }
    public OptimizeRequest getOptimizeRequest() { return optimizeRequest; }
    public String getOriginalPrompt() { return originalPrompt; }
    public String getOptimizationType() { return optimizationType; }
    public String getTargetModel() { return targetModel; }
    public Map<String, Object> getExtraParams() { return extraParams; }
    public boolean isStreamMode() { return streamMode; }
    
    /**
     * Builder 模式
     */
    public static class Builder {
        private StrategyContext context = new StrategyContext();
        
        public Builder generateRequest(PromptRequest request) {
            context.generateRequest = request;
            return this;
        }
        
        public Builder optimizeRequest(OptimizeRequest request) {
            context.optimizeRequest = request;
            if (request != null) {
                context.originalPrompt = request.getPrompt();
                context.optimizationType = request.getOptimizationType();
                context.targetModel = request.getTargetModel();
            }
            return this;
        }
        
        public Builder originalPrompt(String prompt) {
            context.originalPrompt = prompt;
            return this;
        }
        
        public Builder optimizationType(String type) {
            context.optimizationType = type;
            return this;
        }
        
        public Builder targetModel(String model) {
            context.targetModel = model;
            return this;
        }
        
        public Builder extraParams(Map<String, Object> params) {
            context.extraParams = params;
            return this;
        }
        
        public Builder streamMode(boolean stream) {
            context.streamMode = stream;
            return this;
        }
        
        public StrategyContext build() {
            return context;
        }
    }
}