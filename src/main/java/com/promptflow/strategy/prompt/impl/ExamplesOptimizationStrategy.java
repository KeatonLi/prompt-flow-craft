package com.promptflow.strategy.prompt.impl;

import org.springframework.stereotype.Component;

/**
 * 示例优化策略
 * 添加示例，让AI更好地理解期望的输出格式
 */
@Component
public class ExamplesOptimizationStrategy extends AbstractOptimizationStrategy {
    
    @Override
    public String getStrategyType() {
        return "examples";
    }
    
    @Override
    public String getStrategyName() {
        return "示例增强";
    }
    
    @Override
    public String getStrategyDescription() {
        return "添加输入/输出示例，使用少样本学习(Few-shot)技巧，让AI理解期望格式";
    }
    
    @Override
    protected String getOptimizationTypeDescription() {
        return "示例增强优化：\n" +
               "- 添加输入示例（如果适用）\n" +
               "- 添加期望的输出格式示例\n" +
               "- 使用少样本学习(Few-shot)技巧\n" +
               "- 展示正反例对比（如果有助于理解）\n" +
               "- 明确示例与任务的关联";
    }
}