package com.promptflow.service;

import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.PagedResult;
import com.promptflow.entity.PromptCategory;
import com.promptflow.entity.SharedPrompt;
import com.promptflow.repository.PromptCategoryRepository;
import com.promptflow.repository.SharedPromptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharedPromptService {
    
    private static final Logger logger = LoggerFactory.getLogger(SharedPromptService.class);
    
    @Autowired
    private SharedPromptRepository sharedPromptRepository;
    
    @Autowired
    private PromptCategoryRepository categoryRepository;
    
    /**
     * 获取社区提示词列表（分页）
     */
    public PagedResult<SharedPrompt> getCommunityPrompts(int page, int size, String sortBy) {
        logger.info("获取社区提示词列表: page={}, size={}, sortBy={}", page, size, sortBy);
        
        Pageable pageable;
        switch (sortBy) {
            case "likes":
                pageable = PageRequest.of(page, size, Sort.by("likes").descending());
                break;
            case "views":
                pageable = PageRequest.of(page, size, Sort.by("views").descending());
                break;
            default: // latest
                pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        }
        
        Page<SharedPrompt> promptPage = sharedPromptRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
        
        return new PagedResult<>(
            promptPage.getContent(),
            promptPage.getTotalElements(),
            promptPage.getNumber(),
            promptPage.getSize(),
            promptPage.getTotalPages()
        );
    }
    
    /**
     * 获取热门提示词
     */
    public PagedResult<SharedPrompt> getPopularPrompts(int page, int size) {
        logger.info("获取热门提示词: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("likes").descending());
        Page<SharedPrompt> promptPage = sharedPromptRepository.findByIsPublicTrueOrderByLikesDesc(pageable);
        
        return new PagedResult<>(
            promptPage.getContent(),
            promptPage.getTotalElements(),
            promptPage.getNumber(),
            promptPage.getSize(),
            promptPage.getTotalPages()
        );
    }
    
    /**
     * 根据ID获取提示词详情
     */
    public SharedPrompt getPromptById(Long id) {
        logger.info("获取提示词详情: {}", id);
        return sharedPromptRepository.findById(id).orElse(null);
    }
    
    /**
     * 增加浏览量
     */
    public void incrementViews(Long id) {
        sharedPromptRepository.findById(id).ifPresent(prompt -> {
            prompt.setViews(prompt.getViews() + 1);
            sharedPromptRepository.save(prompt);
        });
    }
    
    /**
     * 点赞提示词
     */
    public boolean likePrompt(Long id) {
        logger.info("点赞提示词: {}", id);
        return sharedPromptRepository.findById(id).map(prompt -> {
            prompt.setLikes(prompt.getLikes() + 1);
            sharedPromptRepository.save(prompt);
            return true;
        }).orElse(false);
    }
    
    /**
     * 分享提示词到社区
     */
    public SharedPrompt sharePrompt(String title, String content, String description, String authorName, Long categoryId) {
        logger.info("分享提示词: {}", title);
        
        SharedPrompt prompt = new SharedPrompt();
        prompt.setTitle(title);
        prompt.setContent(content);
        prompt.setDescription(description);
        prompt.setAuthorName(authorName);
        prompt.setIsPublic(true);
        
        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(prompt::setCategory);
        }
        
        return sharedPromptRepository.save(prompt);
    }
    
    /**
     * 搜索社区提示词
     */
    public PagedResult<SharedPrompt> searchPrompts(String keyword, int page, int size) {
        logger.info("搜索社区提示词: keyword={}", keyword);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SharedPrompt> promptPage = sharedPromptRepository
            .findByIsPublicTrueAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        
        return new PagedResult<>(
            promptPage.getContent(),
            promptPage.getTotalElements(),
            promptPage.getNumber(),
            promptPage.getSize(),
            promptPage.getTotalPages()
        );
    }
    
    /**
     * 获取用户的分享
     */
    public List<SharedPrompt> getUserSharedPrompts(String authorName) {
        logger.info("获取用户分享: {}", authorName);
        return sharedPromptRepository.findByAuthorNameOrderByCreatedAtDesc(authorName);
    }
    
    /**
     * 根据分类获取提示词
     */
    public PagedResult<SharedPrompt> getPromptsByCategory(Long categoryId, int page, int size) {
        logger.info("根据分类获取提示词: categoryId={}", categoryId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SharedPrompt> promptPage = sharedPromptRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable);
        
        return new PagedResult<>(
            promptPage.getContent(),
            promptPage.getTotalElements(),
            promptPage.getNumber(),
            promptPage.getSize(),
            promptPage.getTotalPages()
        );
    }
}
