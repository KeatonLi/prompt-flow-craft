package com.promptflow.controller;

import com.promptflow.dto.*;
import com.promptflow.entity.PromptResource;
import com.promptflow.entity.PromptCategory;
import com.promptflow.repository.PromptResourceRepository;
import com.promptflow.service.PromptClassificationService;
import com.promptflow.service.PromptHistoryService;
import com.promptflow.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private PromptResourceRepository promptResourceRepository;

    private static final int PROMPT_PREVIEW_LENGTH = 50;

    /**
     * 保存Agent提示词到历史记录
     */
    @PostMapping("/agent")
    public ResponseEntity<Map<String, Object>> saveAgentPrompt(@RequestBody SaveAgentPromptRequest request) {
        try {
            PromptResource resource = new PromptResource();
            resource.setPromptType("agent");
            resource.setName(request.getName());
            resource.setRoleDescription(request.getRoleDescription());
            resource.setCapabilities(request.getCapabilities());
            resource.setBehaviors(request.getBehaviors());
            resource.setCommunicationStyle(request.getCommunicationStyle());
            resource.setGeneratedPrompt(request.getGeneratedPrompt());
            resource.setPromptSummary(request.getRoleDescription());
            resource.setCreatedAt(LocalDateTime.now());
            resource.setUpdatedAt(LocalDateTime.now());

            PromptResource saved = promptResourceRepository.save(resource);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "保存成功");
            response.put("data", saved.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("保存Agent提示词失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 保存Skill提示词到历史记录
     */
    @PostMapping("/skill")
    public ResponseEntity<Map<String, Object>> saveSkillPrompt(@RequestBody SaveSkillPromptRequest request) {
        try {
            PromptResource resource = new PromptResource();
            resource.setPromptType("skill");
            resource.setName(request.getName());
            resource.setDescription(request.getDescription());
            resource.setSkillType(request.getSkillType());
            resource.setMethod(request.getMethod());
            resource.setEndpoint(request.getEndpoint());
            resource.setParameters(request.getParameters());
            resource.setOutputDescription(request.getOutputDescription());
            resource.setGeneratedPrompt(request.getGeneratedPrompt());
            resource.setPromptSummary(request.getDescription());
            resource.setCreatedAt(LocalDateTime.now());
            resource.setUpdatedAt(LocalDateTime.now());

            PromptResource saved = promptResourceRepository.save(resource);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "保存成功");
            response.put("data", saved.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("保存Skill提示词失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取所有历史记录
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllHistory() {
        try {
            List<PromptResource> historyList = promptHistoryService.getAllHistory();
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
            List<PromptResource> historyList = promptHistoryService.getRecentHistory(limit);
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
            Optional<PromptResource> historyOptional = promptHistoryService.getHistoryById(id);
            
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
            List<PromptResource> historyList = promptHistoryService.searchHistory(keyword);
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
     * 将PromptResource实体转换为HistoryResponse DTO
     * @param resource 提示词资源实体
     * @param isPreview 是否预览模式（预览模式只返回摘要）
     */
    private HistoryResponse convertToHistoryResponse(PromptResource resource, boolean isPreview) {
        String generatedPrompt;
        if (isPreview) {
            // 预览模式：优先使用摘要，否则截取generatedPrompt
            if (resource.getPromptSummary() != null && !resource.getPromptSummary().isEmpty()) {
                generatedPrompt = resource.getPromptSummary();
            } else if (resource.getGeneratedPrompt() != null && resource.getGeneratedPrompt().length() > PROMPT_PREVIEW_LENGTH) {
                generatedPrompt = resource.getGeneratedPrompt().substring(0, PROMPT_PREVIEW_LENGTH) + "...";
            } else {
                generatedPrompt = resource.getGeneratedPrompt();
            }
        } else {
            // 详情模式：返回完整内容
            generatedPrompt = resource.getGeneratedPrompt();
        }

        HistoryResponse response = new HistoryResponse();
        response.setId(resource.getId());
        response.setPromptType(resource.getPromptType());
        response.setName(resource.getName());
        response.setRoleDescription(resource.getRoleDescription());
        response.setCapabilities(resource.getCapabilities());
        response.setBehaviors(resource.getBehaviors());
        response.setCommunicationStyle(resource.getCommunicationStyle());
        response.setDescription(resource.getDescription());
        response.setSkillType(resource.getSkillType());
        response.setMethod(resource.getMethod());
        response.setEndpoint(resource.getEndpoint());
        response.setParameters(resource.getParameters());
        response.setOutputDescription(resource.getOutputDescription());
        response.setGeneratedPrompt(generatedPrompt);
        response.setPromptSummary(resource.getPromptSummary());
        response.setCreatedAt(resource.getCreatedAt());
        response.setLikeCount(resource.getLikeCount());
        response.setViewCount(resource.getViewCount());

        response.setCategoryId(resource.getCategoryId());
        response.setIsAutoTagged(resource.getIsAutoTagged());
        response.setUsageScenario(resource.getUsageScenario());
        response.setEffectivenessScore(resource.getEffectivenessScore());

        if (resource.getCategory() != null) {
            PromptCategory category = resource.getCategory();
            response.setCategory(new HistoryResponse.CategoryResponse(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getColor()
            ));
        }

        if (resource.getAiTags() != null && !resource.getAiTags().isEmpty()) {
            try {
                response.setAiTags(JsonUtil.parseStringList(resource.getAiTags()));
            } catch (Exception e) {
                response.setAiTags(List.of());
            }
        } else {
            response.setAiTags(List.of());
        }

        if (resource.getTags() != null && !resource.getTags().isEmpty()) {
            List<HistoryResponse.TagResponse> tagResponses = resource.getTags().stream()
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
     * 分页查询历史记录（支持筛选和排序）- GET版本
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getHistoryPageGet(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy) {
        try {
            HistoryQueryRequest request = new HistoryQueryRequest();
            request.setPage(page);
            request.setSize(size);
            request.setCategoryId(categoryId);
            request.setKeyword(keyword);
            request.setSortBy(sortBy);
            
            return getHistoryPageInternal(request);
        } catch (Exception e) {
            logger.error("分页查询历史记录失败(GET)", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "分页查询历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 分页查询历史记录（支持筛选和排序）- POST版本
     */
    @PostMapping("/page")
    public ResponseEntity<Map<String, Object>> getHistoryPagePost(@RequestBody HistoryQueryRequest request) {
        try {
            return getHistoryPageInternal(request);
        } catch (Exception e) {
            logger.error("分页查询历史记录失败(POST)", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "分页查询历史记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 内部方法：分页查询历史记录
     */
    private ResponseEntity<Map<String, Object>> getHistoryPageInternal(HistoryQueryRequest request) {
        try {
            PagedResult<PromptResource> result = promptHistoryService.getHistoryPage(request);
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
            List<PromptResource> historyList = promptHistoryService.getHistoryByCategory(categoryId);
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
            PagedResult<PromptResource> result = promptHistoryService.getTopLikedPromptsPage(page, size);
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
            Optional<PromptResource> promptOpt = promptHistoryService.getHistoryById(id);
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
                Optional<PromptResource> updated = promptHistoryService.getHistoryById(id);
                if (updated.isPresent()) {
                    PromptResource pc = updated.get();
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
            Optional<PromptResource> promptOpt = promptHistoryService.getHistoryById(id);

            if (promptOpt.isPresent()) {
                PromptResource pc = promptOpt.get();
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
            PagedResult<PromptResource> result = promptHistoryService.getTopRatedPrompts(page, size);
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
