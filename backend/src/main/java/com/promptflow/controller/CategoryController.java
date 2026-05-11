package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.entity.PromptCategory;
import com.promptflow.repository.PromptCategoryRepository;
import com.promptflow.repository.PromptResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理接口
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private PromptCategoryRepository categoryRepository;

    @Autowired
    private PromptResourceRepository promptResourceRepository;

    /**
     * 获取所有分类
     */
    @GetMapping
    public ApiResponse<List<PromptCategory>> getAllCategories() {
        try {
            List<PromptCategory> categories = categoryRepository.findAll();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error("获取分类列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统预设分类
     */
    @GetMapping("/system")
    public ApiResponse<List<PromptCategory>> getSystemCategories() {
        try {
            List<PromptCategory> categories = categoryRepository.findByIsSystemTrueOrderBySortOrderAsc();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error("获取系统分类失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个分类
     */
    @GetMapping("/{id}")
    public ApiResponse<PromptCategory> getCategoryById(@PathVariable Long id) {
        try {
            return categoryRepository.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("分类不存在"));
        } catch (Exception e) {
            return ApiResponse.error("获取分类失败: " + e.getMessage());
        }
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getCategoryStats() {
        try {
            List<Object[]> categoryCountsRaw = promptResourceRepository.countByCategory();
            Map<Long, Long> categoryCounts = new HashMap<>();
            long totalCount = 0;

            for (Object[] row : categoryCountsRaw) {
                Long categoryId = (Long) row[0];
                Long count = (Long) row[1];
                categoryCounts.put(categoryId, count);
                totalCount += count;
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("categoryCounts", categoryCounts);
            stats.put("totalCount", totalCount);

            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取分类统计失败: " + e.getMessage());
        }
    }
}
