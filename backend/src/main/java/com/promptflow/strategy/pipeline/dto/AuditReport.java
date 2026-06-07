package com.promptflow.strategy.pipeline.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 审计报告
 * QualityAuditStage 输出的结构化审查结果
 */
public class AuditReport {

    /** 总体评分 0-100 */
    private int overallScore;

    /** 问题清单 */
    private List<Issue> issues = new ArrayList<>();

    /** 审计摘要 */
    private String summary;

    /** 是否通过质量阈值的判定 */
    private boolean passed;

    public AuditReport() {}

    public AuditReport(int overallScore, List<Issue> issues, String summary, boolean passed) {
        this.overallScore = overallScore;
        this.issues = issues != null ? issues : new ArrayList<>();
        this.summary = summary;
        this.passed = passed;
    }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public List<Issue> getIssues() { return issues; }
    public void setIssues(List<Issue> issues) { this.issues = issues; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    /**
     * 审计问题
     */
    public static class Issue {
        /** 问题维度：completeness / clarity / structure / executability / safety */
        private String dimension;

        /** 严重级别：critical / major / minor / suggestion */
        private String severity;

        /** 问题描述 */
        private String description;

        /** 改进建议 */
        private String suggestion;

        public Issue() {}

        public Issue(String dimension, String severity, String description, String suggestion) {
            this.dimension = dimension;
            this.severity = severity;
            this.description = description;
            this.suggestion = suggestion;
        }

        public String getDimension() { return dimension; }
        public void setDimension(String dimension) { this.dimension = dimension; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    }
}
