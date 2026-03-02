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
    
    private static final int PROMPT_PREVIEW_LENGTH = 50;
    
    /**
     * 获取所有历史记录
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllHistory() {
        try {
            List<PromptCache> historyList = promptHistoryService.getAllHistory();
            List<HistoryResponse> responseList = historyList.stream()
                .map(p -> convertToHistoryResponse(p, true))
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
                .map(p -> convertToHistoryResponse(p, true))
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
    public ResponseEntity<Map<String, Object>> getHistoryById(@PathVariable("id") Long id) {
        try {
            Optional<PromptCache> historyOptional = promptHistoryService.getHistoryById(id);
            
            if (historyOptional.isPresent()) {
                HistoryResponse response = convertToHistoryResponse(historyOptional.get(), false);
                
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
                .map(p -> convertToHistoryResponse(p, true))
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
     * @param promptCache 提示词缓存实体
     * @param isPreview 是否预览模式（预览模式只返回摘要）
     */
    private HistoryResponse convertToHistoryResponse(PromptCache promptCache, boolean isPreview) {
        String generatedPrompt;
        if (isPreview) {
            // 预览模式：优先使用摘要，否则截取generatedPrompt
            if (promptCache.getPromptSummary() != null && !promptCache.getPromptSummary().isEmpty()) {
                generatedPrompt = promptCache.getPromptSummary();
            } else if (promptCache.getGeneratedPrompt() != null && promptCache.getGeneratedPrompt().length() > PROMPT_PREVIEW_LENGTH) {
                generatedPrompt = promptCache.getGeneratedPrompt().substring(0, PROMPT_PREVIEW_LENGTH) + "...";
            } else {
                generatedPrompt = promptCache.getGeneratedPrompt();
            }
        } else {
            // 详情模式：返回完整内容
            generatedPrompt = promptCache.getGeneratedPrompt();
        }
        
        HistoryResponse response = new HistoryResponse(
            promptCache.getId(),
            promptCache.getTaskDescription(),
            promptCache.getTargetAudience(),
            promptCache.getOutputFormat(),
            promptCache.getConstraints(),
            promptCache.getExamples(),
            promptCache.getTone(),
            promptCache.getLength(),
            generatedPrompt,
            promptCache.getCreatedAt(),
            promptCache.getHitCount()
        );
        
        response.setPromptSummary(promptCache.getPromptSummary());
        
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
                .map(p -> convertToHistoryResponse(p, true))
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
    public ResponseEntity<Map<String, Object>> getHistoryByCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            List<PromptCache> historyList = promptHistoryService.getHistoryByCategory(categoryId);
            List<HistoryResponse> responseList = historyList.stream()
                .map(p -> convertToHistoryResponse(p, true))
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
                .map(p -> convertToHistoryResponse(p, true))
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
    public ResponseEntity<Map<String, Object>> likePrompt(@PathVariable("id") Long id) {
        try {
            boolean success = promptHistoryService.likePrompt(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            if (success) {
                response.put("message", "点赞成功");
            } else {
                response.put("message", "操作太频繁，请60秒后再试");
            }

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
    public ResponseEntity<Map<String, Object>> unlikePrompt(@PathVariable("id") Long id) {
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
            @PathVariable("id") Long id,
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
    public ResponseEntity<Map<String, Object>> deleteHistory(@PathVariable("id") Long id) {
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
    public ResponseEntity<Map<String, Object>> classifyPrompt(@PathVariable("id") Long id) {
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

    /**
     * 评分提示词
     */
    @PostMapping("/{id}/rate")
    public ResponseEntity<Map<String, Object>> ratePrompt(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> ratingRequest) {
        try {
            Integer rating = (Integer) ratingRequest.get("rating");
            String comment = (String) ratingRequest.get("comment");

            if (rating == null || rating < 1 || rating > 5) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "评分必须在1-5之间");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            boolean success = promptHistoryService.ratePrompt(id, rating, comment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            if (success) {
                response.put("message", "评分成功");
                // 返回更新后的评分信息
                Optional<PromptCache> updated = promptHistoryService.getHistoryById(id);
                if (updated.isPresent()) {
                    PromptCache pc = updated.get();
                    response.put("averageRating", pc.getAverageRating());
                    response.put("ratingCount", pc.getRatingCount());
                }
            } else {
                response.put("message", "记录不存在");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("评分失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "评分失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取提示词评分
     */
    @GetMapping("/{id}/rating")
    public ResponseEntity<Map<String, Object>> getPromptRating(@PathVariable("id") Long id) {
        try {
            Optional<PromptCache> promptOpt = promptHistoryService.getHistoryById(id);

            if (promptOpt.isPresent()) {
                PromptCache pc = promptOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("userRating", pc.getUserRating());
                response.put("ratingComment", pc.getRatingComment());
                response.put("averageRating", pc.getAverageRating());
                response.put("ratingCount", pc.getRatingCount());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "记录不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取评分失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取评分失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取评分最高的提示词
     */
    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> getTopRatedPrompts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            PagedResult<PromptCache> result = promptHistoryService.getTopRatedPrompts(page, size);
            List<HistoryResponse> responseList = result.getList().stream()
                .map(p -> convertToHistoryResponse(p, true))
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseList);
            response.put("total", result.getTotal());
            response.put("totalPages", result.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取评分最高的提示词失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取评分最高的提示词失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
