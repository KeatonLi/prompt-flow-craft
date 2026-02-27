package com.promptflow.controller;

import com.promptflow.dto.*;
import com.promptflow.entity.PromptCache;
import com.promptflow.entity.PromptCategory;
import com.promptflow.service.PromptClassificationService;
import com.promptflow.service.PromptHistoryService;
import com.promptflow.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
@CrossOrigin
public class HistoryController {

    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);

    @Autowired
    private PromptHistoryService promptHistoryService;

    @Autowired
    private PromptClassificationService classificationService;
    
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
        HistoryResponse response = new HistoryResponse(
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
        
        response.setCategoryId(promptCache.getCategoryId());
        response.setLikeCount(promptCache.getLikeCount());
        response.setIsAutoTagged(promptCache.getIsAutoTagged());
        response.setUsageScenario(promptCache.getUsageScenario());
        response.setEffectivenessScore(promptCache.getEffectivenessScore());
        
        if (promptCache.getCategory() != null) {
            PromptCategory category = promptCache.getCategory();
            response.setCategory(new HistoryResponse.CategoryResponse(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getColor()
            ));
        }
        
        if (promptCache.getAiTags() != null && !promptCache.getAiTags().isEmpty()) {
            try {
                response.setAiTags(JsonUtil.parseStringList(promptCache.getAiTags()));
            } catch (Exception e) {
                response.setAiTags(List.of());
            }
        } else {
            response.setAiTags(List.of());
        }
        
        if (promptCache.getTags() != null && !promptCache.getTags().isEmpty()) {
            List<HistoryResponse.TagResponse> tagResponses = promptCache.getTags().stream()
                .map(tag -> new HistoryResponse.TagResponse(
                    tag.getId(),
                    tag.getName(),
                    tag.getColor(),
                    tag.getUsageCount()
                ))
                .collect(Collectors.toList());
            response.setTags(tagResponses);
        } else {
            response.setTags(List.of());
        }
        
        return response;
    }

    /**
     * 分页查询历史记录（支持筛选和排序）
     */
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> getHistoryPage(@RequestBody HistoryQueryRequest request) {
        try {
            PagedResult<PromptCache> result = promptHistoryService.getHistoryPage(request);
            List<HistoryResponse> responseList = result.getList().stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            response.put("total", result.getTotal());
            response.put("totalPages", result.getTotalPages());
            response.put("page", result.getPage());
            response.put("size", result.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("分页查询历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "分页查询历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 根据分类查询历史记录
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getHistoryByCategory(@PathVariable Long categoryId) {
        try {
            List<PromptCache> historyList = promptHistoryService.getHistoryByCategory(categoryId);
            List<HistoryResponse> responseList = historyList.stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            response.put("total", responseList.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("根据分类查询历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "根据分类查询历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取点赞数最高的提示词
     */
    @GetMapping("/top-liked")
    public ResponseEntity<Map<String, Object>> getTopLikedPrompts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            PagedResult<PromptCache> result = promptHistoryService.getTopLikedPromptsPage(page, size);
            List<HistoryResponse> responseList = result.getList().stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            response.put("total", result.getTotal());
            response.put("totalPages", result.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取点赞数最高的提示词失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取点赞数最高的提示词失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 点赞提示词
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePrompt(@PathVariable Long id) {
        try {
            boolean success = promptHistoryService.likePrompt(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "点赞成功" : "记录不存在");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("点赞失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "点赞失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 取消点赞提示词
     */
    @PostMapping("/{id}/unlike")
    public ResponseEntity<Map<String, Object>> unlikePrompt(@PathVariable Long id) {
        try {
            boolean success = promptHistoryService.unlikePrompt(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "取消点赞成功" : "记录不存在");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("取消点赞失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "取消点赞失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 更新提示词分类
     */
    @PutMapping("/{id}/category")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @RequestParam Long categoryId) {
        try {
            boolean success = promptHistoryService.updateCategory(id, categoryId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "分类更新成功" : "记录不存在");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("更新分类失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "更新分类失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 删除历史记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHistory(@PathVariable Long id) {
        try {
            boolean success = promptHistoryService.deleteHistory(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "删除成功" : "记录不存在");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("删除历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "删除历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 批量删除历史记录
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteHistory(@RequestBody List<Long> ids) {
        try {
            int count = promptHistoryService.batchDeleteHistory(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "成功删除 " + count + " 条记录");
            response.put("deletedCount", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("批量删除历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "批量删除历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 手动触发单条提示词分类
     */
    @PostMapping("/{id}/classify")
    public ResponseEntity<Map<String, Object>> classifyPrompt(@PathVariable Long id) {
        try {
            Optional<PromptCache> promptOpt = promptHistoryService.getHistoryById(id);
            if (!promptOpt.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "记录不存在");
                return ResponseEntity.notFound().build();
            }

            ClassificationResult result = classificationService.autoClassifyAndTag(promptOpt.get());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            response.put("message", "分类完成");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("分类提示词失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "分类提示词失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 批量处理未分类的提示词
     */
    @PostMapping("/batch-classify")
    public ResponseEntity<Map<String, Object>> batchClassifyPrompts(
            @RequestParam(value = "batchSize", defaultValue = "10") int batchSize) {
        try {
            classificationService.batchClassifyUnTaggedPrompts(batchSize);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量分类任务已启动，处理数量: " + batchSize);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("批量分类失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "批量分类失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
