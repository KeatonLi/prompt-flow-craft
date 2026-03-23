package com.promptflow.strategy.prompt.impl;

import org.springframework.stereotype.Component;

/**
 * 通用优化策略
 * 全面优化提示词的整体质量
 */
@Component
public class GeneralOptimizationStrategy extends AbstractOptimizationStrategy {
    
    @Override
    public String getStrategyType() {
        return "general";
    }
    
    @Override
    public String getStrategyName() {
        return "全面优化";
    }
    
    @Override
    public String getStrategyDescription() {
        return "全面优化提示词的各个维度，包括清晰度、具体性、结构和示例";
    }
    
    @Override
    protected String getOptimizationTypeDescription() {
        return "全面优化（包含以下所有维度）：\n" +
               "1. 清晰度：消除模糊表述，使用明确语言\n" +
               "2. 具体性：添加量化指标和明确标准\n" +
               "3. 结构：优化层次和逻辑顺序\n" +
               "4. 完整性：确保包含角色、任务、约束、输出格式\n" +
               "5. 示例：适当添加输入/输出示例";
    }
}