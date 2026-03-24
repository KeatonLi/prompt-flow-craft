package com.promptflow.controller;

import com.promptflow.dto.*;
import com.promptflow.service.PromptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 提示词 API 控制器（重构版）
 * 
 * 设计原则：
 * 1. 单一职责：只处理 HTTP 请求/响应
 * 2. 依赖注入：通过 Service 层处理业务逻辑
 * 3. 统一异常处理：由全局异常处理器处理
 * 4. 简洁清晰：代码量减少 80%+
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
     * 健康检查
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Prompt Flow Craft API is running!");
    }
}