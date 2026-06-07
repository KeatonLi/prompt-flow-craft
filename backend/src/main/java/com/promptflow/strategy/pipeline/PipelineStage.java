package com.promptflow.strategy.pipeline;

import com.promptflow.dto.llm.LLMRequest;

/**
 * Pipeline 阶段接口
 * 定义流水线中各阶段的行为契约
 * 每个阶段负责构建 LLM 请求并处理响应结果
 */
public interface PipelineStage {

    /**
     * 获取阶段类型标识
     * @return "draft" / "audit" / "refine"
     */
    String getStageType();

    /**
     * 获取阶段显示名称
     * @return 用于前端展示的阶段名称
     */
    String getStageName();

    /**
     * 当前阶段是否需要前序阶段的输出结果
     */
    default boolean requiresPreviousResult() {
        return true;
    }

    /**
     * 构建 LLM 请求
     * @param ctx 流水线上下文（包含当前草稿、审计报告等）
     * @return LLM 请求对象
     */
    LLMRequest buildRequest(PipelineContext ctx);

    /**
     * 处理 LLM 响应内容，并更新上下文
     * @param content  LLM 返回的完整文本
     * @param ctx      流水线上下文（会被修改）
     * @return 处理结果
     */
    PipelineResult processResponse(String content, PipelineContext ctx);
}
