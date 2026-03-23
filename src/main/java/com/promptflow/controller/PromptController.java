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
public class PromptControllerV2 {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptControllerV2.class);
    
    private final PromptService promptService;
    
    @Autowired
    public PromptControllerV2(PromptService promptService) {
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
     * 优化提示词
     * POST /api/prompt/optimize
     */
    @PostMapping("/prompt/optimize")
    public ResponseEntity<ApiResponse<OptimizeResponse>> optimizePrompt(@RequestBody OptimizeRequest request) {
        logger.info("收到提示词优化请求, 类型: {}", request.getOptimizationType());
        
        OptimizeResponse result = promptService.optimizePrompt(request);
        return ResponseEntity.ok(ApiResponse.success("优化完成", result));
    }
    
    /**
     * 分析提示词质量
     * POST /api/prompt/analyze
     */
    @PostMapping("/prompt/analyze")
    public ResponseEntity<ApiResponse<AnalyzeResponse>> analyzePrompt(@RequestBody AnalyzeRequest request) {
        logger.info("收到提示词分析请求");
        
        AnalyzeResponse result = promptService.analyzePrompt(request.getPrompt());
        return ResponseEntity.ok(ApiResponse.success("分析完成", result));
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