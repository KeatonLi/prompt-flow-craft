package com.promptflow.strategy.pipeline;

import com.promptflow.strategy.pipeline.impl.DraftGeneratorStage;
import com.promptflow.strategy.pipeline.impl.PromptRefineStage;
import com.promptflow.strategy.pipeline.impl.QualityAuditStage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pipeline 核心组件单元测试
 */
class PipelineCoreTest {

    @Test
    void testPipelineConfigDefaults() {
        PipelineConfig config = new PipelineConfig();
        assertEquals(2, config.getMaxRounds());
        assertEquals(80, config.getQualityThreshold());
        assertTrue(config.isAuditEnabled());
        assertTrue(config.isRefineEnabled());
        assertEquals("", config.getAuditModel());
        assertEquals("", config.getRefineModel());
    }

    @Test
    void testPipelineConfigGettersSetters() {
        PipelineConfig config = new PipelineConfig();
        config.setMaxRounds(3);
        config.setQualityThreshold(70);
        config.setAuditEnabled(false);
        config.setRefineModel("gpt-4");

        assertEquals(3, config.getMaxRounds());
        assertEquals(70, config.getQualityThreshold());
        assertFalse(config.isAuditEnabled());
        assertEquals("gpt-4", config.getRefineModel());
    }

    @Test
    void testPipelineResultOk() {
        PipelineResult r = PipelineResult.ok("hello");
        assertTrue(r.isSuccess());
        assertEquals("hello", r.getContent());
        assertNull(r.getQualityScore());
        assertFalse(r.isNeedsRefine());
        assertNull(r.getError());
    }

    @Test
    void testPipelineResultAuditResult() {
        PipelineResult r = PipelineResult.auditResult("报告", 85, true);
        assertTrue(r.isSuccess());
        assertEquals("报告", r.getContent());
        assertEquals(85, r.getQualityScore().intValue());
        assertTrue(r.isNeedsRefine());
    }

    @Test
    void testPipelineResultError() {
        PipelineResult r = PipelineResult.error("出错了");
        assertFalse(r.isSuccess());
        assertEquals("出错了", r.getError());
    }

    @Test
    void testPipelineContextBuilderAgent() {
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("测试助手", "你是一个助手", "能力1", "行为1", "专业")
            .streamMode(true)
            .build();

        assertEquals("agent", ctx.getPromptType());
        assertEquals("测试助手", ctx.getAgentName());
        assertEquals("你是一个助手", ctx.getRoleDescription());
        assertEquals("能力1", ctx.getCapabilities());
        assertEquals("行为1", ctx.getBehaviors());
        assertEquals("专业", ctx.getCommunicationStyle());
        assertTrue(ctx.isStreamMode());
    }

    @Test
    void testPipelineContextBuilderSkill() {
        PipelineContext ctx = PipelineContext.builder()
            .promptType("skill")
            .skillFields("天气查询", "查天气", "api", "GET", "/weather", "city", "JSON")
            .build();

        assertEquals("skill", ctx.getPromptType());
        assertEquals("天气查询", ctx.getSkillName());
        assertEquals("查天气", ctx.getSkillDescription());
        assertEquals("api", ctx.getSkillType());
        assertEquals("GET", ctx.getMethod());
        assertEquals("/weather", ctx.getEndpoint());
        assertEquals("city", ctx.getParameters());
        assertEquals("JSON", ctx.getOutputDescription());
    }

    @Test
    void testPipelineContextRoundAndDraft() {
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("test", "role", null, null, null)
            .build();

        assertEquals(0, ctx.getCurrentRound());
        assertEquals(-1, ctx.getLatestScore());

        ctx.setCurrentDraft("草稿内容");
        assertEquals("草稿内容", ctx.getCurrentDraft());

        ctx.setCurrentRound(1);
        ctx.incrementRound();
        assertEquals(2, ctx.getCurrentRound());

        // 审计历史
        ctx.getAuditHistory().add(new PipelineContext.AuditRound(0, 75, "报告", false));
        assertEquals(1, ctx.getAuditHistory().size());
        assertEquals(75, ctx.getLatestScore());
        assertEquals(0, ctx.getAuditHistory().get(0).getRound());
    }

    @Test
    void testDraftGeneratorStageFiltersPreface() {
        DraftGeneratorStage stage = new DraftGeneratorStage();

        // 模拟有前置解释 + # 开头的正式内容
        String rawWithPreface = "我来帮你生成这个提示词\n# 测试助手\n## 角色定义\n你是一个助手";
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("测试助手", "角色", null, null, null)
            .build();

        PipelineResult result = stage.processResponse(rawWithPreface, ctx);
        assertTrue(result.isSuccess());
        assertEquals("# 测试助手\n## 角色定义\n你是一个助手", result.getContent());
        assertEquals("# 测试助手\n## 角色定义\n你是一个助手", ctx.getCurrentDraft());
    }

    @Test
    void testDraftGeneratorStageHandlesAlreadyClean() {
        DraftGeneratorStage stage = new DraftGeneratorStage();

        // 已经是干净的 # 开头
        String clean = "# 测试助手\n## 角色定义";
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("测试助手", "角色", null, null, null)
            .build();

        PipelineResult result = stage.processResponse(clean, ctx);
        assertTrue(result.isSuccess());
        assertEquals("# 测试助手\n## 角色定义", result.getContent());
    }

    @Test
    void testDraftGeneratorStageHandlesNoHash() {
        DraftGeneratorStage stage = new DraftGeneratorStage();

        // 没有 # 开头的，保持原样
        String noHash = "你是一个助手，请帮我...";
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("测试", "角色", null, null, null)
            .build();

        PipelineResult result = stage.processResponse(noHash, ctx);
        assertTrue(result.isSuccess());
        assertEquals("你是一个助手，请帮我...", result.getContent());
    }

    @Test
    void testDraftGeneratorStageRejectsEmpty() {
        DraftGeneratorStage stage = new DraftGeneratorStage();
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("", "", null, null, null)
            .build();

        PipelineResult result = stage.processResponse("   ", ctx);
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testQualityAuditStageParseJsonSuccess() {
        QualityAuditStage stage = new QualityAuditStage(new PipelineConfig());
        String json = "{\"overallScore\": 85, \"summary\": \"整体良好\", \"issues\": []}";
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("test", "role", null, null, null)
            .build();
        ctx.setCurrentDraft("# 草稿内容");

        PipelineResult result = stage.processResponse(json, ctx);
        assertTrue(result.isSuccess());
        assertEquals(85, result.getQualityScore().intValue());
        assertFalse(result.isNeedsRefine()); // 85 ≥ 80, 不需要继续
    }

    @Test
    void testQualityAuditStageParseJsonLowScore() {
        QualityAuditStage stage = new QualityAuditStage(new PipelineConfig());
        String json = "{\"overallScore\": 55, \"summary\": \"需要改进\", \"issues\": []}";
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("test", "role", null, null, null)
            .build();
        ctx.setCurrentDraft("草稿");

        PipelineResult result = stage.processResponse(json, ctx);
        assertTrue(result.isSuccess());
        assertEquals(55, result.getQualityScore().intValue());
        assertTrue(result.isNeedsRefine()); // 55 < 80, round 0 < maxRounds-1
    }

    @Test
    void testQualityAuditStageHandlesMalformedJson() {
        QualityAuditStage stage = new QualityAuditStage(new PipelineConfig());
        String malformed = "这不是JSON，这是一段文本描述";
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("test", "role", null, null, null)
            .build();
        ctx.setCurrentDraft("草稿");

        PipelineResult result = stage.processResponse(malformed, ctx);
        assertTrue(result.isSuccess()); // 宽松模式返回成功
        assertEquals(60, result.getQualityScore().intValue()); // 回退到 60 分
    }

    @Test
    void testQualityAuditStageRejectsEmpty() {
        QualityAuditStage stage = new QualityAuditStage(new PipelineConfig());
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("", "", null, null, null)
            .build();
        ctx.setCurrentDraft("# 草稿");

        PipelineResult result = stage.processResponse("", ctx);
        assertFalse(result.isSuccess());
    }

    @Test
    void testPromptRefineStageProcessResponse() {
        PromptRefineStage stage = new PromptRefineStage(new PipelineConfig());
        PipelineContext ctx = PipelineContext.builder()
            .promptType("agent")
            .agentFields("测试助手", "角色", null, null, null)
            .build();
        ctx.setCurrentDraft("旧草稿");

        String refined = "# 测试助手\n## 优化后的版本\n更加清晰了";
        PipelineResult result = stage.processResponse(refined, ctx);
        assertTrue(result.isSuccess());
        assertEquals(refined, result.getContent());
        assertEquals(refined, ctx.getCurrentDraft());
    }
}
