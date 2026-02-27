package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.PromptRequest;
import com.promptflow.dto.PromptResponse;
import com.promptflow.service.PromptGenerationService;
import com.promptflow.service.PromptCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173", "http://111.231.107.210:3000"}, 
           allowCredentials = "true", 
           allowedHeaders = "*", 
           methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PromptController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptController.class);
    
    private final PromptGenerationService promptGenerationService;
    
    @Autowired
    private PromptCacheService promptCacheService;
    
    public PromptController(PromptGenerationService promptGenerationService) {
        this.promptGenerationService = promptGenerationService;
    }
    
    @PostMapping("/generate-prompt")
    public ResponseEntity<ApiResponse<String>> generatePrompt(@RequestBody PromptRequest request) {
        try {
            logger.info("Received prompt generation request: {}", request);
            
            // 验证请求
            if (request.getTaskDescription() == null || request.getTaskDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "任务描述不能为空"));
            }
            
            String generatedPrompt = promptGenerationService.generatePrompt(request);
            
            logger.info("Successfully generated prompt for task: {}", request.getTaskDescription());
            
            return ResponseEntity.ok(ApiResponse.success("提示词生成成功", generatedPrompt));
            
        } catch (Exception e) {
            logger.error("Error processing prompt generation request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "生成提示词时发生错误: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Prompt Flow Craft API is running!");
    }
    
    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        try {
            PromptCacheService.CacheStats stats = promptCacheService.getCacheStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total_cache_count", stats.getTotalCacheCount());
            response.put("total_hit_count", stats.getTotalHitCount());
            response.put("hit_rate", String.format("%.2f%%", stats.getHitRate() * 100));
            response.put("message", "缓存统计获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取缓存统计时发生错误", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取缓存统计失败: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}