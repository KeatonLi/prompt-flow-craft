package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.entity.PromptCategory;
import com.promptflow.repository.PromptCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private PromptCategoryRepository categoryRepository;

    /**
     * 获取所有分类
     */
    @GetMapping
    public ApiResponse<List<PromptCategory>> getAllCategories() {
        logger.info("获取所有分类");
        List<PromptCategory> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        return ApiResponse.success(categories);
    }

    /**
     * 获取系统预设分类
     */
    @GetMapping("/system")
    public ApiResponse<List<PromptCategory>> getSystemCategories() {
        logger.info("获取系统预设分类");
        List<PromptCategory> categories = categoryRepository.findByIsSystemTrueOrderBySortOrderAsc();
        return ApiResponse.success(categories);
    }

    /**
     * 获取单个分类详情
     */
    @GetMapping("/{id}")
    public ApiResponse<PromptCategory> getCategoryById(@PathVariable Long id) {
        logger.info("获取分类详情: {}", id);
        return categoryRepository.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "分类不存在"));
    }

    /**
     * 创建分类
     */
    @PostMapping
    public ApiResponse<PromptCategory> createCategory(@RequestBody PromptCategory category) {
        logger.info("创建分类: {}", category.getName());

        if (categoryRepository.existsByName(category.getName())) {
            return ApiResponse.error(400, "分类名称已存在");
        }

        category.setIsSystem(false);
        PromptCategory saved = categoryRepository.save(category);
        return ApiResponse.success(saved);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ApiResponse<PromptCategory> updateCategory(@PathVariable Long id, @RequestBody PromptCategory category) {
        logger.info("更新分类: {}", id);

        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    existing.setDescription(category.getDescription());
                    existing.setIcon(category.getIcon());
                    existing.setColor(category.getColor());
                    existing.setSortOrder(category.getSortOrder());
                    PromptCategory updated = categoryRepository.save(existing);
                    return ApiResponse.success(updated);
                })
                .orElse(ApiResponse.error(404, "分类不存在"));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        logger.info("删除分类: {}", id);

        return categoryRepository.findById(id)
                .map(category -> {
                    if (Boolean.TRUE.equals(category.getIsSystem())) {
                        return ApiResponse.<Void>error(400, "系统预设分类不能删除");
                    }
                    categoryRepository.delete(category);
                    return ApiResponse.<Void>success(null);
                })
                .orElse(ApiResponse.error(404, "分类不存在"));
    }
}
