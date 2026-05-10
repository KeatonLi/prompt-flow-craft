package com.promptflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.*;
import com.promptflow.entity.AgentPrompt;
import com.promptflow.entity.SkillPrompt;
import com.promptflow.service.PromptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.function.Consumer;

/**
 * 提示词 API 控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"},
           allowCredentials = "false",
           allowedHeaders = "*",
           methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class PromptController {

    private static final Logger logger = LoggerFactory.getLogger(PromptController.class);

    private final PromptService promptService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    /**
     * 同步生成提示词
     * POST /api/generate-prompt
     */
    @PostMapping("/generate-prompt")
    public ResponseEntity<ApiResponse<String>> generatePrompt(@RequestBody PromptRequest request) {
        logger.info("收到提示词生成请求: {}", request.getTaskDescription());

        String result = promptService.generatePrompt(request);

        // 保存到历史记录
        try {
            promptService.saveGeneralPrompt(request.getTaskDescription(), result);
        } catch (Exception e) {
            logger.error("保存提示词失败", e);
        }

        return ResponseEntity.ok(ApiResponse.success("提示词生成成功", result));
    }

    /**
     * 流式生成提示词（SSE）
     * POST /api/generate-prompt/stream
     */
    @PostMapping(value = "/generate-prompt/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generatePromptStream(@RequestBody PromptRequest request) {
        logger.info("收到流式提示词生成请求: {}", request.getTaskDescription());

        return promptService.generatePromptStream(request, null, null);
    }

    /**
     * 生成 Agent 提示词（同步）
     * POST /api/generate-agent
     */
    @PostMapping("/generate-agent")
    public ResponseEntity<ApiResponse<AgentPrompt>> generateAgent(@RequestBody AgentPromptRequest request) {
        logger.info("收到Agent提示词生成请求: {}", request.getName());

        String generatedPrompt = promptService.generateAgentPromptText(
            request.getName(),
            request.getRoleDescription(),
            request.getCapabilities(),
            request.getBehaviors(),
            request.getCommunicationStyle()
        );

        // 保存到数据库
        AgentPrompt saved = null;
        try {
            saved = promptService.saveAgentPrompt(
                request.getName(),
                request.getRoleDescription(),
                request.getCapabilities(),
                request.getBehaviors(),
                request.getCommunicationStyle(),
                generatedPrompt
            );
        } catch (Exception e) {
            logger.error("保存Agent提示词失败", e);
        }

        return ResponseEntity.ok(ApiResponse.success("Agent提示词生成成功", saved != null ? saved : new AgentPrompt()));
    }

    /**
     * 生成 Agent 提示词（流式）
     * POST /api/generate-agent/stream
     */
    @PostMapping(value = "/generate-agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateAgentStream(@RequestBody AgentPromptRequest request) {
        logger.info("收到Agent提示词流式生成请求: {}", request.getName());

        return promptService.generateAgentPromptStream(
            request.getName(),
            request.getRoleDescription(),
            request.getCapabilities(),
            request.getBehaviors(),
            request.getCommunicationStyle(),
            (generatedPrompt) -> {
                // 保存到数据库
                try {
                    promptService.saveAgentPrompt(
                        request.getName(),
                        request.getRoleDescription(),
                        request.getCapabilities(),
                        request.getBehaviors(),
                        request.getCommunicationStyle(),
                        generatedPrompt
                    );
                } catch (Exception e) {
                    logger.error("保存Agent提示词失败", e);
                }
            },
            null,
            null
        );
    }

    /**
     * 生成 Skill 提示词（同步）
     * POST /api/generate-skill
     */
    @PostMapping("/generate-skill")
    public ResponseEntity<ApiResponse<SkillPrompt>> generateSkill(@RequestBody SkillPromptRequest request) {
        logger.info("收到Skill提示词生成请求: {}", request.getName());

        String generatedPrompt = promptService.generateSkillPromptText(
            request.getName(),
            request.getDescription(),
            request.getSkillType(),
            request.getMethod(),
            request.getEndpoint(),
            request.getParameters(),
            request.getOutputDescription()
        );

        // 保存到数据库
        SkillPrompt saved = null;
        try {
            saved = promptService.saveSkillPrompt(
                request.getName(),
                request.getDescription(),
                request.getSkillType(),
                request.getMethod(),
                request.getEndpoint(),
                request.getParameters(),
                request.getOutputDescription(),
                generatedPrompt
            );
        } catch (Exception e) {
            logger.error("保存Skill提示词失败", e);
        }

        return ResponseEntity.ok(ApiResponse.success("Skill提示词生成成功", saved != null ? saved : new SkillPrompt()));
    }

    /**
     * 生成 Skill 提示词（流式）
     * POST /api/generate-skill/stream
     */
    @PostMapping(value = "/generate-skill/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateSkillStream(@RequestBody SkillPromptRequest request) {
        logger.info("收到Skill提示词流式生成请求: {}", request.getName());

        return promptService.generateSkillPromptStream(
            request.getName(),
            request.getDescription(),
            request.getSkillType(),
            request.getMethod(),
            request.getEndpoint(),
            request.getParameters(),
            request.getOutputDescription(),
            (generatedPrompt) -> {
                // 保存到数据库
                try {
                    promptService.saveSkillPrompt(
                        request.getName(),
                        request.getDescription(),
                        request.getSkillType(),
                        request.getMethod(),
                        request.getEndpoint(),
                        request.getParameters(),
                        request.getOutputDescription(),
                        generatedPrompt
                    );
                } catch (Exception e) {
                    logger.error("保存Skill提示词失败", e);
                }
            },
            null,
            null
        );
    }

    /**
     * 健康检查
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Prompt Flow Craft API is running!");
    }

    // 请求 DTO
    public static class AgentPromptRequest {
        private String name;
        private String roleDescription;
        private String capabilities;
        private String behaviors;
        private String communicationStyle;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRoleDescription() { return roleDescription; }
        public void setRoleDescription(String roleDescription) { this.roleDescription = roleDescription; }
        public String getCapabilities() { return capabilities; }
        public void setCapabilities(String capabilities) { this.capabilities = capabilities; }
        public String getBehaviors() { return behaviors; }
        public void setBehaviors(String behaviors) { this.behaviors = behaviors; }
        public String getCommunicationStyle() { return communicationStyle; }
        public void setCommunicationStyle(String communicationStyle) { this.communicationStyle = communicationStyle; }
    }

    public static class SkillPromptRequest {
        private String name;
        private String description;
        private SkillPrompt.SkillType skillType;
        private String method;
        private String endpoint;
        private String parameters;
        private String outputDescription;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public SkillPrompt.SkillType getSkillType() { return skillType; }
        public void setSkillType(SkillPrompt.SkillType skillType) { this.skillType = skillType; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public String getParameters() { return parameters; }
        public void setParameters(String parameters) { this.parameters = parameters; }
        public String getOutputDescription() { return outputDescription; }
        public void setOutputDescription(String outputDescription) { this.outputDescription = outputDescription; }
    }
}