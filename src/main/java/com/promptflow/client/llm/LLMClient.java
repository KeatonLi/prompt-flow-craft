package com.promptflow.client.llm;

import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.dto.llm.LLMResponse;

import java.util.function.Consumer;

/**
 * LLM 客户端接口抽象
 * 支持不同 LLM 提供商（OpenAI、Claude、通义千问等）
 */
public interface LLMClient {
    
    /**
     * 同步调用 LLM
     * @param request LLM 请求
     * @return LLM 响应
     */
    LLMResponse call(LLMRequest request);
    
    /**
     * 流式调用 LLM
     * @param request LLM 请求
     * @param onContent 内容回调
     * @param onComplete 完成回调
     * @param onError 错误回调
     */
    void callStream(LLMRequest request, 
                    Consumer<String> onContent,
                    Runnable onComplete,
                    Consumer<Throwable> onError);
    
    /**
     * 获取客户端类型
     * @return 客户端类型标识
     */
    String getClientType();
}