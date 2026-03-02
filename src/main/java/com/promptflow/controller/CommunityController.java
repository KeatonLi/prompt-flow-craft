package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.PagedResult;
import com.promptflow.entity.SharedPrompt;
import com.promptflow.service.SharedPromptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*")
public class CommunityController {

    private static final Logger logger = LoggerFactory.getLogger(CommunityController.class);

    @Autowired
    private SharedPromptService sharedPromptService;

    /**
     * 获取社区提示词列表
     */
    @GetMapping("/prompts")
    public ApiResponse<PagedResult<SharedPrompt>> getCommunityPrompts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "latest") String sortBy) {
        logger.info("获取社区提示词列表: page={}, size={}, sortBy={}", page, size, sortBy);
        PagedResult<SharedPrompt> result = sharedPromptService.getCommunityPrompts(page, size, sortBy);
        return ApiResponse.success(result);
    }

    /**
     * 获取热门提示词
     */
    @GetMapping("/prompts/popular")
    public ApiResponse<PagedResult<SharedPrompt>> getPopularPrompts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("获取热门提示词: page={}, size={}", page, size);
        PagedResult<SharedPrompt> result = sharedPromptService.getPopularPrompts(page, size);
        return ApiResponse.success(result);
    }

    /**
     * 获取提示词详情
     */
    @GetMapping("/prompts/{id}")
    public ApiResponse<SharedPrompt> getPromptById(@PathVariable Long id) {
        logger.info("获取提示词详情: {}", id);
        SharedPrompt prompt = sharedPromptService.getPromptById(id);
        if (prompt == null) {
            return ApiResponse.error(404, "提示词不存在");
        }
        // 增加浏览量
        sharedPromptService.incrementViews(id);
        return ApiResponse.success(prompt);
    }

    /**
     * 点赞提示词
     */
    @PostMapping("/prompts/{id}/like")
    public ApiResponse<Boolean> likePrompt(@PathVariable Long id) {
        logger.info("点赞提示词: {}", id);
        boolean success = sharedPromptService.likePrompt(id);
        if (success) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.error(404, "提示词不存在");
        }
    }

    /**
     * 分享提示词到社区
     */
    @PostMapping("/prompts")
    public ApiResponse<SharedPrompt> sharePrompt(@RequestBody Map<String, Object> request) {
        logger.info("分享提示词到社区");
        
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        String description = (String) request.get("description");
        String authorName = (String) request.get("authorName");
        Long categoryId = request.get("categoryId") != null ? 
            ((Number) request.get("categoryId")).longValue() : null;
        
        if (title == null || content == null || authorName == null) {
            return ApiResponse.error(400, "标题、内容和作者名称不能为空");
        }
        
        SharedPrompt prompt = sharedPromptService.sharePrompt(title, content, description, authorName, categoryId);
        return ApiResponse.success(prompt);
    }

    /**
     * 搜索社区提示词
     */
    @GetMapping("/prompts/search")
    public ApiResponse<PagedResult<SharedPrompt>> searchPrompts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        logger.info("搜索社区提示词: keyword={}", keyword);
        PagedResult<SharedPrompt> result = sharedPromptService.searchPrompts(keyword, page, size);
        return ApiResponse.success(result);
    }

    /**
     * 获取用户分享
     */
    @GetMapping("/prompts/user/{authorName}")
    public ApiResponse<List<SharedPrompt>> getUserSharedPrompts(@PathVariable String authorName) {
        logger.info("获取用户分享: {}", authorName);
        List<SharedPrompt> prompts = sharedPromptService.getUserSharedPrompts(authorName);
        return ApiResponse.success(prompts);
    }

    /**
     * 根据分类获取提示词
     */
    @GetMapping("/prompts/category/{categoryId}")
    public ApiResponse<PagedResult<SharedPrompt>> getPromptsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        logger.info("根据分类获取提示词: categoryId={}", categoryId);
        PagedResult<SharedPrompt> result = sharedPromptService.getPromptsByCategory(categoryId, page, size);
        return ApiResponse.success(result);
    }
}
