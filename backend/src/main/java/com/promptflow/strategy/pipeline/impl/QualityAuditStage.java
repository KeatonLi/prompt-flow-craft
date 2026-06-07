package com.promptflow.strategy.pipeline.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.strategy.pipeline.PipelineConfig;
import com.promptflow.strategy.pipeline.PipelineContext;
import com.promptflow.strategy.pipeline.PipelineContext.AuditRound;
import com.promptflow.strategy.pipeline.PipelineResult;
import com.promptflow.strategy.pipeline.PipelineStage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 质量审计阶段（S2）
 * 审查提示词草稿质量，输出结构化审计报告
 */
@Component
public class QualityAuditStage implements PipelineStage {

    private static final Logger log = LoggerFactory.getLogger(QualityAuditStage.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.model}")
    private String defaultModel;

    private final PipelineConfig pipelineConfig;

    private String auditTemplate;

    public QualityAuditStage(PipelineConfig pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
    }

    @PostConstruct
    public void init() {
        this.auditTemplate = loadTemplate("prompts/audit_prompt_template.txt");
        log.info("QualityAuditStage 模板加载完成");
    }

    private String loadTemplate(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("找不到模板: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取模板失败: " + path, e);
        }
    }

    @Override
    public String getStageType() {
        return "audit";
    }

    @Override
    public String getStageName() {
        return "质量审查";
    }

    @Override
    public LLMRequest buildRequest(PipelineContext ctx) {
        String currentDraft = ctx.getCurrentDraft();
        if (currentDraft == null || currentDraft.trim().isEmpty()) {
            throw new IllegalStateException("审计阶段缺少提示词草稿");
        }

        String userPrompt = auditTemplate.replace("{currentDraft}", currentDraft);

        String model = pipelineConfig.getAuditModel();
        if (model == null || model.isBlank()) {
            model = defaultModel;
        }

        return LLMRequest.builder()
            .model(model)
            .messages(List.of(LLMRequest.Message.user(userPrompt)))
            .temperature(0.3) // 审计使用较低温度确保一致性
            .maxTokens(2000)
            .stream(ctx.isStreamMode())
            .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PipelineResult processResponse(String content, PipelineContext ctx) {
        if (content == null || content.trim().isEmpty()) {
            return PipelineResult.error("审计结果为空");
        }

        try {
            // 提取 JSON 对象
            String jsonStr = content;
            int jsonStart = content.indexOf('{');
            int jsonEnd = content.lastIndexOf('}');
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                jsonStr = content.substring(jsonStart, jsonEnd + 1);
            }

            Map<String, Object> report = objectMapper.readValue(jsonStr, Map.class);

            int overallScore = report.get("overallScore") instanceof Number
                ? ((Number) report.get("overallScore")).intValue() : 50;
            String summary = (String) report.getOrDefault("summary", "");

            // 判定是否通过
            boolean passed = overallScore >= pipelineConfig.getQualityThreshold();

            // 保存审计报告文本
            ctx.setAuditReportText(content);

            // 记录审计历史
            ctx.getAuditHistory().add(new AuditRound(
                ctx.getCurrentRound(), overallScore, content, passed));

            boolean needsRefine = !passed
                && ctx.getCurrentRound() < pipelineConfig.getMaxRounds() - 1;

            log.info("审计完成: 评分={}, 通过={}, 需要精炼={}, 轮次={}/{}",
                overallScore, passed, needsRefine, ctx.getCurrentRound() + 1, pipelineConfig.getMaxRounds());

            return PipelineResult.auditResult(content, overallScore, needsRefine);

        } catch (Exception e) {
            log.warn("解析审计JSON失败，使用宽松模式: {}", e.getMessage());
            // 解析失败时保守处理：默认 60 分，需要精炼
            ctx.setAuditReportText(content);
            ctx.getAuditHistory().add(new AuditRound(
                ctx.getCurrentRound(), 60, content, false));

            boolean needsRefine = ctx.getCurrentRound() < pipelineConfig.getMaxRounds() - 1;
            return PipelineResult.auditResult(content, 60, needsRefine);
        }
    }
}
