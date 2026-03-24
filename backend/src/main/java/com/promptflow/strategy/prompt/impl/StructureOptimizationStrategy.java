package com.promptflow.strategy.prompt.impl;

import org.springframework.stereotype.Component;

/**
 * 结构优化策略
 * 优化提示词的结构，使其更有逻辑性
 */
@Component
public class StructureOptimizationStrategy extends AbstractOptimizationStrategy {
    
    @Override
    public String getStrategyType() {
        return "structure";
    }
    
    @Override
    public String getStrategyName() {
        return "结构优化";
    }
    
    @Override
    public String getStrategyDescription() {
        return "优化提示词的结构组织，添加清晰的层次和逻辑顺序";
    }
    
    @Override
    protected String getOptimizationTypeDescription() {
        return "结构优化：\n" +
               "- 使用清晰的层次结构（角色-任务-约束-输出）\n" +
               "- 添加适当的标题和分隔\n" +
               "- 按逻辑顺序组织信息\n" +
               "- 使用编号列表或项目符号\n" +
               "- 确保信息分组合理";
    }
}