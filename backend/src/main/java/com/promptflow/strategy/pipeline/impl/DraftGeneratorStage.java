package com.promptflow.strategy.pipeline.impl;

import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.strategy.pipeline.PipelineContext;
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

/**
 * 草稿生成阶段（S1）
 * 根据用户输入的 Agent/Skill 表单信息生成初始提示词草稿
 */
@Component
public class DraftGeneratorStage implements PipelineStage {

    private static final Logger log = LoggerFactory.getLogger(DraftGeneratorStage.class);

    @Value("${api.model}")
    private String defaultModel;

    @Value("${api.temperature:0.7}")
    private double defaultTemperature;

    @Value("${api.max-tokens:4000}")
    private int defaultMaxTokens;

    private String agentDraftTemplate;
    private String skillDraftTemplate;

    @PostConstruct
    public void init() {
        this.agentDraftTemplate = loadTemplate("prompts/draft_agent_prompt_template.txt");
        this.skillDraftTemplate = loadTemplate("prompts/draft_skill_prompt_template.txt");
        log.info("DraftGeneratorStage 模板加载完成");
    }

    private String loadTemplate(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("找不到模板: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取模板失败: " + path, e);
        }
    }

    private String replacePlaceholders(String template, Object... keyValues) {
        String result = template;
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = (String) keyValues[i];
            String value = keyValues[i + 1] != null ? (String) keyValues[i + 1] : "";
            result = result.replace("{" + key + "}", value);
        }
        return result;
    }

    @Override
    public String getStageType() {
        return "draft";
    }

    @Override
    public String getStageName() {
        return "草稿生成";
    }

    @Override
    public boolean requiresPreviousResult() {
        return false;
    }

    @Override
    public LLMRequest buildRequest(PipelineContext ctx) {
        String systemPrompt;
        if ("agent".equals(ctx.getPromptType())) {
            systemPrompt = replacePlaceholders(agentDraftTemplate,
                "name", ctx.getAgentName() != null ? ctx.getAgentName() : "",
                "roleDescription", ctx.getRoleDescription() != null ? ctx.getRoleDescription() : "",
                "capabilities", ctx.getCapabilities() != null ? ctx.getCapabilities() : "",
                "behaviors", ctx.getBehaviors() != null ? ctx.getBehaviors() : "",
                "communicationStyle", ctx.getCommunicationStyle() != null ? ctx.getCommunicationStyle() : "");
        } else {
            systemPrompt = replacePlaceholders(skillDraftTemplate,
                "name", ctx.getSkillName() != null ? ctx.getSkillName() : "",
                "description", ctx.getSkillDescription() != null ? ctx.getSkillDescription() : "",
                "skillType", ctx.getSkillType() != null ? ctx.getSkillType() : "",
                "method", ctx.getMethod() != null ? ctx.getMethod() : "",
                "endpoint", ctx.getEndpoint() != null ? ctx.getEndpoint() : "",
                "parameters", ctx.getParameters() != null ? ctx.getParameters() : "",
                "outputDescription", ctx.getOutputDescription() != null ? ctx.getOutputDescription() : "");
        }

        return LLMRequest.builder()
            .model(defaultModel)
            .messages(List.of(LLMRequest.Message.user(systemPrompt)))
            .temperature(defaultTemperature)
            .maxTokens(defaultMaxTokens)
            .stream(ctx.isStreamMode())
            .build();
    }

    @Override
    public PipelineResult processResponse(String content, PipelineContext ctx) {
        if (content == null || content.trim().isEmpty()) {
            return PipelineResult.error("草稿生成结果为空白");
        }
        // 过滤模型在正式提示词之前的解释性输出
        // 查找 "# " 作为真正内容的开始标记
        String trimmed = content.trim();
        int promptStart = -1;

        // 检查开头是否是 "# "
        if (trimmed.startsWith("# ")) {
            promptStart = 0;
        } else {
            // 在字符串中查找 "\n# "
            int idx = trimmed.indexOf("\n# ");
            if (idx >= 0) {
                promptStart = idx + 1;
            }
        }

        String cleanContent;
        if (promptStart > 0) {
            cleanContent = trimmed.substring(promptStart);
            log.debug("过滤了 {} 个字符的前置内容", promptStart);
        } else {
            cleanContent = trimmed;
        }

        // 保存草稿到上下文
        ctx.setCurrentDraft(cleanContent);
        return PipelineResult.ok(cleanContent);
    }
}
