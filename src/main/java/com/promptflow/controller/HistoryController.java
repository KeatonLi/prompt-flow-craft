package com.promptflow.controller;

import com.promptflow.dto.HistoryResponse;
import com.promptflow.entity.PromptCache;
import com.promptflow.service.PromptHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173"}, 
               allowCredentials = "true", 
               allowedHeaders = "*")
public class HistoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    
    @Autowired
    private PromptHistoryService promptHistoryService;
    
    /**
     * 获取所有历史记录
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllHistory() {
        try {
            List<PromptCache> historyList = promptHistoryService.getAllHistory();
            List<HistoryResponse> responseList = historyList.stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            response.put("total", responseList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取最近的历史记录
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentHistory(@RequestParam(value = "limit", defaultValue = "20") int limit) {
        try {
            List<PromptCache> historyList = promptHistoryService.getRecentHistory(limit);
            List<HistoryResponse> responseList = historyList.stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取最近历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取最近历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取历史记录详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getHistoryById(@PathVariable Long id) {
        try {
            Optional<PromptCache> historyOptional = promptHistoryService.getHistoryById(id);
            
            if (historyOptional.isPresent()) {
                HistoryResponse response = convertToHistoryResponse(historyOptional.get());
                
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("success", true);
                responseMap.put("data", response);
                
                return ResponseEntity.ok(responseMap);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "历史记录不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取历史记录详情失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取历史记录详情失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 搜索历史记录
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchHistory(@RequestParam(value = "keyword") String keyword) {
        try {
            List<PromptCache> historyList = promptHistoryService.searchHistory(keyword);
            List<HistoryResponse> responseList = historyList.stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            response.put("total", responseList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("搜索历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "搜索历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 将PromptCache实体转换为HistoryResponse DTO
     */
    private HistoryResponse convertToHistoryResponse(PromptCache promptCache) {
        return new HistoryResponse(
            promptCache.getId(),
            promptCache.getTaskDescription(),
            promptCache.getTargetAudience(),
            promptCache.getOutputFormat(),
            promptCache.getConstraints(),
            promptCache.getExamples(),
            promptCache.getTone(),
            promptCache.getLength(),
            promptCache.getGeneratedPrompt(),
            promptCache.getCreatedAt(),
            promptCache.getHitCount()
        );
    }
}