package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.entity.PromptTag;
import com.promptflow.repository.PromptTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private PromptTagRepository tagRepository;

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
}
