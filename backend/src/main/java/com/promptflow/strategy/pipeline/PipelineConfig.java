package com.promptflow.strategy.pipeline;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Pipeline 配置
 * 从 application.yml 读取流水线参数
 */
@Component
@ConfigurationProperties(prefix = "pipeline")
public class PipelineConfig {

    /** 最大审查轮数（草稿→审计→精炼计为 1 轮） */
    private int maxRounds = 2;

    /** 质量合格分数线（0-100），高于此分数跳过精炼 */
    private int qualityThreshold = 80;

    /** 是否启用审计阶段 */
    private boolean auditEnabled = true;

    /** 是否启用精炼阶段 */
    private boolean refineEnabled = true;

    /** 审计模型（默认使用主模型，可单独指定） */
    private String auditModel = "";

    /** 精炼模型 */
    private String refineModel = "";

    // Getters & Setters
    public int getMaxRounds() { return maxRounds; }
    public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }

    public int getQualityThreshold() { return qualityThreshold; }
    public void setQualityThreshold(int qualityThreshold) { this.qualityThreshold = qualityThreshold; }

    public boolean isAuditEnabled() { return auditEnabled; }
    public void setAuditEnabled(boolean auditEnabled) { this.auditEnabled = auditEnabled; }

    public boolean isRefineEnabled() { return refineEnabled; }
    public void setRefineEnabled(boolean refineEnabled) { this.refineEnabled = refineEnabled; }

    public String getAuditModel() { return auditModel; }
    public void setAuditModel(String auditModel) { this.auditModel = auditModel; }

    public String getRefineModel() { return refineModel; }
    public void setRefineModel(String refineModel) { this.refineModel = refineModel; }
}
