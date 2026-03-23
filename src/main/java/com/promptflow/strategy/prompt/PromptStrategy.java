package com.promptflow.strategy.prompt;

import com.promptflow.dto.llm.LLMRequest;

/**
 * 提示词策略接口
 * 定义不同类型的提示词生成/优化策略
 */
public interface PromptStrategy {
    
    /**
     * 获取策略类型
     * @return 策略类型标识
     */
    String getStrategyType();
    
    /**
     * 获取策略名称
     * @return 策略名称（用于展示）
     */
    String getStrategyName();
    
    /**
     * 获取策略描述
     * @return 策略描述
     */
    String getStrategyDescription();
    
    /**
     * 构建 LLM 请求
     * @param context 策略上下文（包含用户输入等）
     * @return LLM 请求
     */
    LLMRequest buildRequest(StrategyContext context);
    
    /**
     * 解析 LLM 响应
     * @param content 响应内容
     * @return 解析后的结果
     */
    Object parseResponse(String content);
}