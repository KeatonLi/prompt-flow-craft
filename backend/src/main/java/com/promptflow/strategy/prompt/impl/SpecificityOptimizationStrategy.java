package com.promptflow.strategy.prompt.impl;

import org.springframework.stereotype.Component;

/**
 * 具体性优化策略
 * 增加提示词的具体性和细节，减少模糊性
 */
@Component
public class SpecificityOptimizationStrategy extends AbstractOptimizationStrategy {
    
    @Override
    public String getStrategyType() {
        return "specificity";
    }
    
    @Override
    public String getStrategyName() {
        return "具体性优化";
    }
    
    @Override
    public String getStrategyDescription() {
        return "增加提示词的具体性和细节，添加量化指标和明确标准，减少模糊性";
    }
    
    @Override
    protected String getOptimizationTypeDescription() {
        return "具体性优化：\n" +
               "- 添加具体的量化指标（字数限制、数量要求等）\n" +
               "- 明确质量标准（专业水平、详细程度等）\n" +
               "- 指定具体的领域或场景\n" +
               "- 添加具体的条件约束\n" +
               "- 使用具体的例子说明期望的输出";
    }
}