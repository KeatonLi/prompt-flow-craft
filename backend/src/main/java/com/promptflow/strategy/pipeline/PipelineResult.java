package com.promptflow.strategy.pipeline;

/**
 * Pipeline 阶段处理结果
 * 封装阶段执行后的产出，供编排器判断流程走向
 */
public class PipelineResult {

    /** 阶段是否成功完成 */
    private boolean success;

    /** 产出的文本内容（如草稿文本、审计报告文本等） */
    private String content;

    /** 可选的审计评分（仅审计阶段有意义） */
    private Integer qualityScore;

    /** 是否需要继续下一轮循环（仅审计阶段可设置） */
    private boolean needsRefine;

    /** 错误信息（失败时填写） */
    private String error;

    public PipelineResult() {}

    public static PipelineResult ok(String content) {
        PipelineResult r = new PipelineResult();
        r.success = true;
        r.content = content;
        return r;
    }

    public static PipelineResult auditResult(String content, int score, boolean needsRefine) {
        PipelineResult r = new PipelineResult();
        r.success = true;
        r.content = content;
        r.qualityScore = score;
        r.needsRefine = needsRefine;
        return r;
    }

    public static PipelineResult error(String message) {
        PipelineResult r = new PipelineResult();
        r.success = false;
        r.error = message;
        return r;
    }

    // Getters & Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getQualityScore() { return qualityScore; }
    public void setQualityScore(Integer qualityScore) { this.qualityScore = qualityScore; }
    public boolean isNeedsRefine() { return needsRefine; }
    public void setNeedsRefine(boolean needsRefine) { this.needsRefine = needsRefine; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
