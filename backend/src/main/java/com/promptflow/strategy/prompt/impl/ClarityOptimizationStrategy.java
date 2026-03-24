package com.promptflow.strategy.prompt.impl;

import org.springframework.stereotype.Component;

/**
 * 清晰度优化策略
 * 提升提示词的清晰度，让AI更准确地理解任务要求
 */
@Component
public class ClarityOptimizationStrategy extends AbstractOptimizationStrategy {
    
    @Override
    public String getStrategyType() {
        return "clarity";
    }
    
    @Override
    public String getStrategyName() {
        return "清晰度优化";
    }
    
    @Override
    public String getStrategyDescription() {
        return "提升提示词的清晰度，消除模糊表述，让AI更准确地理解任务要求";
    }
    
    @Override
    protected String getOptimizationTypeDescription() {
        return "清晰度优化：\n" +
               "- 消除模糊表述，使用具体明确的语言\n" +
               "- 明确任务的目标和范围\n" +
               "- 使用清晰的指令动词（生成、分析、计算等）\n" +
               "- 避免使用可能导致歧义的词汇\n" +
               "- 确保逻辑顺序清晰";
    }
}