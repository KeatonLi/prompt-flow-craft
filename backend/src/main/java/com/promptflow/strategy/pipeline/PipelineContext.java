package com.promptflow.strategy.pipeline;

import com.promptflow.dto.PromptRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pipeline 上下文
 * 承载整个流水线执行过程中的全部数据，在阶段之间传递
 */
public class PipelineContext {

    // ========== 原始输入 ==========

    /** 用户原始请求（通用 PromptRequest 格式） */
    private PromptRequest originalRequest;

    /** 请求类型：agent / skill */
    private String promptType;

    // ========== Agent 表单字段 ==========
    private String agentName;
    private String roleDescription;
    private String capabilities;
    private String behaviors;
    private String communicationStyle;

    // ========== Skill 表单字段 ==========
    private String skillName;
    private String skillDescription;
    private String skillType;
    private String method;
    private String endpoint;
    private String parameters;
    private String outputDescription;

    // ========== 运行时状态 ==========

    /** 当前最新草稿 */
    private String currentDraft;

    /** 最新审计报告文本 */
    private String auditReportText;

    /** 审计评分历史（按轮次） */
    private final List<AuditRound> auditHistory = new ArrayList<>();

    /** 当前循环轮次（从 0 开始） */
    private int currentRound;

    /** 各阶段输出归档 key = "stageType_round" */
    private final Map<String, Object> stageOutputs = new LinkedHashMap<>();

    /** 是否流式输出 */
    private boolean streamMode;

    // ========== 构造方法 ==========

    private PipelineContext() {}

    public static Builder builder() {
        return new Builder();
    }

    // ========== 嵌套类型 ==========

    /** 审计轮次记录 */
    public static class AuditRound {
        private final int round;
        private final int score;
        private final String report;
        private final boolean passed;

        public AuditRound(int round, int score, String report, boolean passed) {
            this.round = round;
            this.score = score;
            this.report = report;
            this.passed = passed;
        }

        public int getRound() { return round; }
        public int getScore() { return score; }
        public String getReport() { return report; }
        public boolean isPassed() { return passed; }
    }

    // ========== Getters ==========

    public PromptRequest getOriginalRequest() { return originalRequest; }
    public String getPromptType() { return promptType; }

    public String getAgentName() { return agentName; }
    public String getRoleDescription() { return roleDescription; }
    public String getCapabilities() { return capabilities; }
    public String getBehaviors() { return behaviors; }
    public String getCommunicationStyle() { return communicationStyle; }

    public String getSkillName() { return skillName; }
    public String getSkillDescription() { return skillDescription; }
    public String getSkillType() { return skillType; }
    public String getMethod() { return method; }
    public String getEndpoint() { return endpoint; }
    public String getParameters() { return parameters; }
    public String getOutputDescription() { return outputDescription; }

    public String getCurrentDraft() { return currentDraft; }
    public void setCurrentDraft(String draft) { this.currentDraft = draft; }

    public String getAuditReportText() { return auditReportText; }
    public void setAuditReportText(String text) { this.auditReportText = text; }

    public List<AuditRound> getAuditHistory() { return auditHistory; }
    public int getCurrentRound() { return currentRound; }
    public void setCurrentRound(int round) { this.currentRound = round; }
    public void incrementRound() { this.currentRound++; }

    public Map<String, Object> getStageOutputs() { return stageOutputs; }
    public boolean isStreamMode() { return streamMode; }
    public void setStreamMode(boolean streamMode) { this.streamMode = streamMode; }

    /** 获取最新审计评分，无审计记录返回 -1 */
    public int getLatestScore() {
        if (auditHistory.isEmpty()) return -1;
        return auditHistory.get(auditHistory.size() - 1).getScore();
    }

    // ========== Builder ==========

    public static class Builder {
        private final PipelineContext ctx = new PipelineContext();

        public Builder originalRequest(PromptRequest req) {
            ctx.originalRequest = req;
            return this;
        }

        public Builder promptType(String type) {
            ctx.promptType = type;
            return this;
        }

        // Agent 字段
        public Builder agentFields(String name, String role, String capabilities,
                                   String behaviors, String communicationStyle) {
            ctx.agentName = name;
            ctx.roleDescription = role;
            ctx.capabilities = capabilities;
            ctx.behaviors = behaviors;
            ctx.communicationStyle = communicationStyle;
            return this;
        }

        // Skill 字段
        public Builder skillFields(String name, String description, String skillType,
                                   String method, String endpoint, String parameters,
                                   String outputDescription) {
            ctx.skillName = name;
            ctx.skillDescription = description;
            ctx.skillType = skillType;
            ctx.method = method;
            ctx.endpoint = endpoint;
            ctx.parameters = parameters;
            ctx.outputDescription = outputDescription;
            return this;
        }

        public Builder streamMode(boolean stream) {
            ctx.streamMode = stream;
            return this;
        }

        public PipelineContext build() {
            return ctx;
        }
    }
}
