package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.entity.PromptCache;
import com.promptflow.entity.PromptTag;
import com.promptflow.repository.PromptCacheRepository;
import com.promptflow.repository.PromptTagRepository;
import com.promptflow.service.PromptClassificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private PromptTagRepository tagRepository;

    @Autowired
    private PromptCacheRepository promptCacheRepository;

    @Autowired
    private PromptClassificationService classificationService;

    /**
     * 获取所有标签
     */
    @GetMapping
    public ApiResponse<List<PromptTag>> getAllTags() {
        logger.info("获取所有标签");
        List<PromptTag> tags = tagRepository.findAllByOrderByUsageCountDesc();
        return ApiResponse.success(tags);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    public ApiResponse<List<PromptTag>> getHotTags() {
        logger.info("获取热门标签");
        List<PromptTag> tags = tagRepository.findTop10ByOrderByUsageCountDesc();
        return ApiResponse.success(tags);
    }

    /**
     * 获取系统预设标签
     */
    @GetMapping("/system")
    public ApiResponse<List<PromptTag>> getSystemTags() {
        logger.info("获取系统预设标签");
        List<PromptTag> tags = tagRepository.findByIsSystemTrueOrderByUsageCountDesc();
        return ApiResponse.success(tags);
    }

    /**
     * 搜索标签
     */
    @GetMapping("/search")
    public ApiResponse<List<PromptTag>> searchTags(@RequestParam String keyword) {
        logger.info("搜索标签: {}", keyword);
        List<PromptTag> tags = tagRepository.findByNameContainingIgnoreCaseOrderByUsageCountDesc(keyword);
        return ApiResponse.success(tags);
    }

    /**
     * 获取单个标签详情
     */
    @GetMapping("/{id}")
    public ApiResponse<PromptTag> getTagById(@PathVariable Long id) {
        logger.info("获取标签详情: {}", id);
        return tagRepository.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "标签不存在"));
    }

    /**
     * 根据名称获取标签
     */
    @GetMapping("/name/{name}")
    public ApiResponse<PromptTag> getTagByName(@PathVariable String name) {
        logger.info("获取标签: {}", name);
        return tagRepository.findByName(name)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "标签不存在"));
    }

    /**
     * 创建标签
     */
    @PostMapping
    public ApiResponse<PromptTag> createTag(@RequestBody PromptTag tag) {
        logger.info("创建标签: {}", tag.getName());

        if (tagRepository.existsByName(tag.getName())) {
            return ApiResponse.error(400, "标签名称已存在");
        }

        tag.setIsSystem(false);
        tag.setUsageCount(0);
        PromptTag saved = tagRepository.save(tag);
        return ApiResponse.success(saved);
    }

    /**
     * 批量创建标签
     */
    @PostMapping("/batch")
    public ApiResponse<List<PromptTag>> batchCreateTags(@RequestBody List<String> tagNames) {
        logger.info("批量创建标签: {}", tagNames);

        List<PromptTag> savedTags = tagNames.stream()
                .filter(name -> !tagRepository.existsByName(name))
                .map(name -> {
                    PromptTag tag = new PromptTag();
                    tag.setName(name);
                    tag.setIsSystem(false);
                    tag.setUsageCount(0);
                    return tagRepository.save(tag);
                })
                .toList();

        return ApiResponse.success(savedTags);
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public ApiResponse<PromptTag> updateTag(@PathVariable Long id, @RequestBody PromptTag tag) {
        logger.info("更新标签: {}", id);

        return tagRepository.findById(id)
                .map(existing -> {
                    existing.setName(tag.getName());
                    existing.setColor(tag.getColor());
                    PromptTag updated = tagRepository.save(existing);
                    return ApiResponse.success(updated);
                })
                .orElse(ApiResponse.error(404, "标签不存在"));
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        logger.info("删除标签: {}", id);

        return tagRepository.findById(id)
                .map(tag -> {
                    if (Boolean.TRUE.equals(tag.getIsSystem())) {
                        return ApiResponse.<Void>error(400, "系统预设标签不能删除");
                    }
                    tagRepository.delete(tag);
                    return ApiResponse.<Void>success(null);
                })
                .orElse(ApiResponse.error(404, "标签不存在"));
    }

    /**
     * 获取提示词的标签
     */
    @GetMapping("/prompt/{promptId}")
    public ApiResponse<Set<PromptTag>> getTagsByPromptId(@PathVariable Long promptId) {
        logger.info("获取提示词的标签: {}", promptId);
        Set<PromptTag> tags = tagRepository.findByPromptId(promptId);
        return ApiResponse.success(tags);
    }

    /**
     * 获取未打标签的提示词数量
     */
    @GetMapping("/admin/untagged-count")
    public ApiResponse<Map<String, Object>> getUntaggedCount() {
        logger.info("获取未打标签的提示词数量");
        try {
            // 使用分页查询避免一次性加载太多数据
            Page<PromptCache> page = promptCacheRepository.findUnTaggedPromptsPage(PageRequest.of(0, 1));
            long total = page.getTotalElements();
            Map<String, Object> result = new HashMap<>();
            result.put("count", total);
            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("获取未打标签数量失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 批量自动打标签
     * @param batchSize 每次处理的条数，默认50
     */
    @PostMapping("/admin/auto-tag")
    public ApiResponse<Map<String, Object>> autoTagPrompts(@RequestParam(name = "batchSize", defaultValue = "50") Integer batchSize) {
        logger.info("开始批量自动打标签，批量大小: {}", batchSize);

        try {
            // 使用分页查询获取未打标签的提示词
            int pageSize = Math.min(batchSize, 100); // 最多一次查100条
            Page<PromptCache> page = promptCacheRepository.findUnTaggedPromptsPage(PageRequest.of(0, pageSize));
            List<PromptCache> untagged = page.getContent();
            long totalRemaining = page.getTotalElements() - pageSize;

            if (untagged.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("total", 0);
                result.put("processed", 0);
                result.put("message", "所有提示词都已经有标签");
                return ApiResponse.success(result);
            }

            int successCount = 0;
            int failCount = 0;
            String lastError = null;

            for (int i = 0; i < untagged.size(); i++) {
                try {
                    PromptCache prompt = untagged.get(i);
                    classificationService.autoClassifyAndTag(prompt);
                    successCount++;

                    // 添加小延迟避免请求过快
                    if (i % 10 == 0) {
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    failCount++;
                    lastError = e.getMessage();
                    logger.error("自动打标签失败: id={}, error={}", untagged.get(i).getId(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotalElements());
            result.put("processed", successCount);
            result.put("failed", failCount);
            result.put("remaining", totalRemaining + failCount);
            if (lastError != null) {
                result.put("lastError", lastError);
            }
            result.put("message", String.format("成功处理 %d 条，剩余约 %d 条", successCount, Math.max(0, totalRemaining)));

            logger.info("批量自动打标签完成: 总数 {}, 成功 {}, 失败 {}, 剩余 {}",
                        page.getTotalElements(), successCount, failCount, Math.max(0, totalRemaining));

            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("批量打标签异常", e);
            return ApiResponse.error(500, "批量打标签失败: " + e.getMessage());
        }
    }

    /**
     * 为指定提示词打标签
     */
    @PostMapping("/admin/tag/{promptId}")
    public ApiResponse<Map<String, Object>> tagSinglePrompt(@PathVariable Long promptId) {
        logger.info("为提示词打标签: {}", promptId);

        return promptCacheRepository.findById(promptId)
                .map(prompt -> {
                    try {
                        classificationService.autoClassifyAndTag(prompt);
                        Map<String, Object> result = new HashMap<>();
                        result.put("promptId", promptId);
                        result.put("success", true);
                        result.put("aiTags", prompt.getAiTags());
                        return ApiResponse.<Map<String, Object>>success(result);
                    } catch (Exception e) {
                        logger.error("打标签失败: id={}", promptId, e);
                        return ApiResponse.<Map<String, Object>>error(500, "打标签失败: " + e.getMessage());
                    }
                })
                .orElse(ApiResponse.<Map<String, Object>>error(404, "提示词不存在"));
    }
}
