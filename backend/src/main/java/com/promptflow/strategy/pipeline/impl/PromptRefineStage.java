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
import java.util.stream.Collectors;

/**
 * 精炼优化阶段（S3）
 * 根据审计报告对提示词草稿进行针对性优化
 */
@Component
public class PromptRefineStage implements PipelineStage {

    private static final Logger log = LoggerFactory.getLogger(PromptRefineStage.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.model}")
    private String defaultModel;

    @Value("${api.temperature:0.7}")
    private double defaultTemperature;

    @Value("${api.max-tokens:4000}")
    private int defaultMaxTokens;

    private final PipelineConfig pipelineConfig;

    private String refineTemplate;

    public PromptRefineStage(PipelineConfig pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
    }

    @PostConstruct
    public void init() {
        this.refineTemplate = loadTemplate("prompts/refine_prompt_template.txt");
        log.info("PromptRefineStage 模板加载完成");
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
        return "refine";
    }

    @Override
    public String getStageName() {
        return "精炼优化";
    }

    @Override
    @SuppressWarnings("unchecked")
    public LLMRequest buildRequest(PipelineContext ctx) {
        String currentDraft = ctx.getCurrentDraft();
        if (currentDraft == null || currentDraft.trim().isEmpty()) {
            throw new IllegalStateException("精炼阶段缺少提示词草稿");
        }

        AuditRound latestAudit = ctx.getAuditHistory().isEmpty()
            ? null : ctx.getAuditHistory().get(ctx.getAuditHistory().size() - 1);

        int score = latestAudit != null ? latestAudit.getScore() : 60;
        String summary = "";
        String issues = "";

        if (latestAudit != null && latestAudit.getReport() != null) {
            try {
                String report = latestAudit.getReport();
                int jsonStart = report.indexOf('{');
                int jsonEnd = report.lastIndexOf('}');
                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                    Map<String, Object> reportMap = objectMapper.readValue(
                        report.substring(jsonStart, jsonEnd + 1), Map.class);
                    summary = (String) reportMap.getOrDefault("summary", "");

                    List<Map<String, Object>> issueList =
                        (List<Map<String, Object>>) reportMap.get("issues");
                    if (issueList != null) {
                        issues = issueList.stream()
                            .map(i -> String.format("- [%s][%s] %s → %s",
                                i.get("severity"), i.get("dimension"),
                                i.get("description"), i.get("suggestion")))
                            .collect(Collectors.joining("\n"));
                    }
                }
            } catch (Exception e) {
                log.warn("解析精炼模板的审计数据失败", e);
            }
        }

        String promptName = "agent".equals(ctx.getPromptType())
            ? (ctx.getAgentName() != null ? ctx.getAgentName() : "")
            : (ctx.getSkillName() != null ? ctx.getSkillName() : "");

        String userPrompt = refineTemplate
            .replace("{overallScore}", String.valueOf(score))
            .replace("{auditSummary}", summary != null ? summary : "")
            .replace("{auditIssues}", issues != null && !issues.isEmpty() ? issues : "无需特别修改")
            .replace("{currentDraft}", currentDraft)
            .replace("{promptName}", promptName);

        String model = pipelineConfig.getRefineModel();
        if (model == null || model.isBlank()) {
            model = defaultModel;
        }

        return LLMRequest.builder()
            .model(model)
            .messages(List.of(LLMRequest.Message.user(userPrompt)))
            .temperature(defaultTemperature)
            .maxTokens(defaultMaxTokens)
            .stream(ctx.isStreamMode())
            .build();
    }

    @Override
    public PipelineResult processResponse(String content, PipelineContext ctx) {
        if (content == null || content.trim().isEmpty()) {
            return PipelineResult.error("精炼结果为空");
        }
        // 更新当前草稿为精炼后的版本
        String trimmed = content.trim();
        ctx.setCurrentDraft(trimmed);
        log.info("精炼完成: 输出长度={}", trimmed.length());
        return PipelineResult.ok(trimmed);
    }
}
