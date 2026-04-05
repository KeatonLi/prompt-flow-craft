package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.entity.PromptTag;
import com.promptflow.repository.PromptTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理接口
 */
@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    @Autowired
    private PromptTagRepository tagRepository;

    /**
     * 获取所有标签
     */
    @GetMapping
    public ApiResponse<List<PromptTag>> getAllTags() {
        try {
            List<PromptTag> tags = tagRepository.findAll();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.error("获取标签列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    public ApiResponse<List<PromptTag>> getHotTags() {
        try {
            List<PromptTag> tags = tagRepository.findByIsSystemTrueOrderByUsageCountDesc();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.error("获取热门标签失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取标签
     */
    @GetMapping("/{id}")
    public ApiResponse<PromptTag> getTagById(@PathVariable Long id) {
        try {
            return tagRepository.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("标签不存在"));
        } catch (Exception e) {
            return ApiResponse.error("获取标签失败: " + e.getMessage());
        }
    }
}
