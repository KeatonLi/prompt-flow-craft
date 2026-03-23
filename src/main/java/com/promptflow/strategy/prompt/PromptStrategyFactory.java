package com.promptflow.strategy.prompt;

import com.promptflow.strategy.prompt.impl.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提示词策略工厂
 * 管理和提供策略实例
 */
@Component
public class PromptStrategyFactory {
    
    private final Map<String, PromptStrategy> strategies = new ConcurrentHashMap<>();
    
    public PromptStrategyFactory(
            GeneratePromptStrategy generateStrategy,
            ClarityOptimizationStrategy clarityStrategy,
            SpecificityOptimizationStrategy specificityStrategy,
            ExamplesOptimizationStrategy examplesStrategy,
            StructureOptimizationStrategy structureStrategy,
            GeneralOptimizationStrategy generalStrategy) {
        
        // 注册生成策略
        register(generateStrategy);
        
        // 注册优化策略
        register(clarityStrategy);
        register(specificityStrategy);
        register(examplesStrategy);
        register(structureStrategy);
        register(generalStrategy);
    }
    
    /**
     * 注册策略
     */
    public void register(PromptStrategy strategy) {
        strategies.put(strategy.getStrategyType(), strategy);
    }
    
    /**
     * 获取策略
     * @param type 策略类型
     * @return 策略实例，如果不存在返回通用优化策略
     */
    public PromptStrategy getStrategy(String type) {
        PromptStrategy strategy = strategies.get(type);
        if (strategy == null) {
            // 默认返回通用优化策略
            return strategies.get("general");
        }
        return strategy;
    }
    
    /**
     * 获取所有优化策略
     * @return 优化策略列表
     */
    public List<PromptStrategy> getOptimizationStrategies() {
        return strategies.values().stream()
            .filter(s -> !s.getStrategyType().equals("generate"))
            .toList();
    }
    
    /**
     * 判断策略是否存在
     */
    public boolean hasStrategy(String type) {
        return strategies.containsKey(type);
    }
}