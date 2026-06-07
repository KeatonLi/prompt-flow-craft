package com.promptflow.strategy.pipeline;

import com.promptflow.client.llm.LLMClient;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.strategy.pipeline.impl.DraftGeneratorStage;
import com.promptflow.strategy.pipeline.impl.PromptRefineStage;
import com.promptflow.strategy.pipeline.impl.QualityAuditStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Pipeline 编排器
 * 编排草稿生成 → 质量审计 → 条件循环精炼的完整流程
 */
@Component
public class PipelineOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(PipelineOrchestrator.class);
    private static final long SSE_TIMEOUT = 300000L; // 5 分钟

    private final LLMClient llmClient;
    private final PipelineConfig config;
    private final DraftGeneratorStage draftStage;
    private final QualityAuditStage auditStage;
    private final PromptRefineStage refineStage;

    public PipelineOrchestrator(LLMClient llmClient,
                                PipelineConfig config,
                                DraftGeneratorStage draftStage,
                                QualityAuditStage auditStage,
                                PromptRefineStage refineStage) {
        this.llmClient = llmClient;
        this.config = config;
        this.draftStage = draftStage;
        this.auditStage = auditStage;
        this.refineStage = refineStage;
    }

    /**
     * 同步执行 Pipeline（非流式）
     * @return 最终输出的提示词文本
     */
    public String execute(PipelineContext ctx) {
        ctx.setStreamMode(false);

        // S1: 草稿生成
        PipelineResult draftResult = executeStageSync(draftStage, ctx);
        if (!draftResult.isSuccess()) {
            throw new RuntimeException("草稿生成失败: " + draftResult.getError());
        }

        // S2→S3 循环
        for (int round = 0; round < config.getMaxRounds(); round++) {
            ctx.setCurrentRound(round);

            // S2: 审计
            PipelineResult auditResult = executeStageSync(auditStage, ctx);
            if (!auditResult.isSuccess()) {
                log.warn("审计阶段失败(round={})，跳过精炼: {}", round, auditResult.getError());
                break;
            }

            if (auditResult.isNeedsRefine() && config.isRefineEnabled()) {
                // S3: 精炼
                PipelineResult refineResult = executeStageSync(refineStage, ctx);
                if (!refineResult.isSuccess()) {
                    log.warn("精炼阶段失败(round={})，使用当前草稿: {}", round, refineResult.getError());
                    break;
                }
            } else {
                log.info("审计通过(评分={})，结束流水线", auditResult.getQualityScore());
                break;
            }
        }

        return ctx.getCurrentDraft();
    }

    /**
     * 流式执行 Pipeline
     * 通过 SSE 逐阶段推送流式内容
     */
    public SseEmitter executeStream(PipelineContext ctx) {
        ctx.setStreamMode(true);
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                // S1: 草稿生成
                sendEvent(emitter, "stage-start",
                    "{\"stage\":\"draft\",\"name\":\"草稿生成\",\"round\":" + ctx.getCurrentRound() + "}");

                executeStageStream(draftStage, ctx, emitter);
                sendEvent(emitter, "stage-complete",
                    "{\"stage\":\"draft\",\"status\":\"ok\"}");

                // S2→S3 循环
                for (int round = 0; round < config.getMaxRounds(); round++) {
                    ctx.setCurrentRound(round);

                    // S2: 审计
                    sendEvent(emitter, "stage-start",
                        "{\"stage\":\"audit\",\"name\":\"质量审查\",\"round\":" + round + "}");
                    PipelineResult auditResult = executeStageStream(auditStage, ctx, emitter);

                    if (!auditResult.isSuccess()) {
                        sendEvent(emitter, "stage-complete",
                            "{\"stage\":\"audit\",\"status\":\"error\",\"message\":\"" +
                                escapeJson(auditResult.getError()) + "\"}");
                        break;
                    }

                    int score = auditResult.getQualityScore() != null ? auditResult.getQualityScore() : 0;
                    boolean needsRefine = auditResult.isNeedsRefine() && config.isRefineEnabled();

                    sendEvent(emitter, "stage-complete",
                        "{\"stage\":\"audit\",\"status\":\"ok\",\"score\":" + score +
                            ",\"needsRefine\":" + needsRefine + "}");

                    if (!needsRefine) {
                        log.info("审计通过(评分={})，结束流水线", score);
                        break;
                    }

                    // S3: 精炼
                    sendEvent(emitter, "stage-start",
                        "{\"stage\":\"refine\",\"name\":\"精炼优化\",\"round\":" + round + "}");
                    PipelineResult refineResult = executeStageStream(refineStage, ctx, emitter);

                    if (!refineResult.isSuccess()) {
                        sendEvent(emitter, "stage-complete",
                            "{\"stage\":\"refine\",\"status\":\"error\",\"message\":\"" +
                                escapeJson(refineResult.getError()) + "\"}");
                        break;
                    }

                    sendEvent(emitter, "stage-complete",
                        "{\"stage\":\"refine\",\"status\":\"ok\"}");
                }

                // 发送完成事件
                sendEvent(emitter, "done", "{\"done\":true}");
                emitter.complete();

            } catch (Exception e) {
                log.error("Pipeline 流式执行出错", e);
                handleError(emitter, e);
            } finally {
                executor.shutdown();
            }
        });

        emitter.onCompletion(() -> executor.shutdown());
        emitter.onTimeout(() -> executor.shutdown());
        emitter.onError(e -> executor.shutdown());

        return emitter;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 同步执行单个阶段
     */
    private PipelineResult executeStageSync(PipelineStage stage, PipelineContext ctx) {
        LLMRequest request = stage.buildRequest(ctx);
        var response = llmClient.call(request);
        if (!response.isSuccess()) {
            return PipelineResult.error(response.getError() != null
                ? response.getError().getMessage() : "LLM调用失败");
        }
        return stage.processResponse(response.getContent(), ctx);
    }

    /**
     * 流式执行单个阶段，将 LLM 输出实时推送给客户端
     * @return 阶段处理结果（内含完整内容）
     */
    private PipelineResult executeStageStream(PipelineStage stage,
                                              PipelineContext ctx,
                                              SseEmitter emitter) {
        LLMRequest request = stage.buildRequest(ctx);
        StringBuilder fullContent = new StringBuilder();

        // 草稿阶段需要实时过滤模型的前置解释文本
        Consumer<String> contentFilter = "draft".equals(stage.getStageType())
            ? createDraftContentFilter(emitter, fullContent)
            : chunk -> {
                fullContent.append(chunk);
                sendEvent(emitter, "message", chunk);
              };

        llmClient.callStream(request,
            chunk -> contentFilter.accept(chunk),
            () -> {
                // 流完成，无事
            },
            error -> {
                log.error("阶段流式调用出错: {}", error.getMessage());
            }
        );

        return stage.processResponse(fullContent.toString(), ctx);
    }

    /**
     * 创建草稿阶段的内容过滤器，实时过滤模型在前置解释文本
     * 只从 "# " 标记开始往客户端推送内容
     */
    private Consumer<String> createDraftContentFilter(SseEmitter emitter,
                                                       StringBuilder fullContent) {
        StringBuilder pendingBuffer = new StringBuilder();
        AtomicBoolean foundPromptStart = new AtomicBoolean(false);
        AtomicInteger lastSentIndex = new AtomicInteger(0);

        return chunk -> {
            synchronized (fullContent) {
                fullContent.append(chunk);
            }
            synchronized (pendingBuffer) {
                pendingBuffer.append(chunk);

                if (!foundPromptStart.get()) {
                    String pending = pendingBuffer.toString();
                    int promptStart = -1;

                    // 检查开头是否是 "# "
                    if (pending.startsWith("# ")) {
                        promptStart = 0;
                    } else {
                        int idx = pending.indexOf("\n# ");
                        if (idx >= 0) {
                            promptStart = idx + 1;
                        }
                    }

                    if (promptStart >= 0) {
                        foundPromptStart.set(true);
                        String actualContent = pending.substring(promptStart);
                        pendingBuffer.setLength(0);
                        pendingBuffer.append(actualContent);
                        lastSentIndex.set(actualContent.length());
                        if (!actualContent.isEmpty()) {
                            sendEvent(emitter, "message", actualContent);
                        }
                    }
                } else {
                    // 已经找到了，发送新增的内容
                    String newContent = pendingBuffer.toString();
                    int sent = lastSentIndex.get();
                    if (newContent.length() > sent) {
                        String toSend = newContent.substring(sent);
                        lastSentIndex.set(newContent.length());
                        if (!toSend.isEmpty()) {
                            sendEvent(emitter, "message", toSend);
                        }
                    }
                }
            }
        };
    }

    private void sendEvent(SseEmitter emitter, String event, String data) {
        try {
            emitter.send(SseEmitter.event()
                .name(event)
                .data(data));
        } catch (IOException e) {
            log.warn("发送 SSE 事件失败: event={}", event, e);
        }
    }

    private void handleError(SseEmitter emitter, Throwable error) {
        try {
            sendEvent(emitter, "error",
                "{\"error\":\"" + escapeJson(error.getMessage()) + "\"}");
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
