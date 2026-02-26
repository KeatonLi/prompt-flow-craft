package com.promptflow.service;

import com.promptflow.dto.HistoryQueryRequest;
import com.promptflow.dto.PagedResult;
import com.promptflow.entity.PromptCache;
import com.promptflow.repository.PromptCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromptHistoryService {
    
    @Autowired
    private PromptCacheRepository promptCacheRepository;
    
    /**
     * 获取所有历史记录
     * @return 历史记录列表
     */
    public List<PromptCache> getAllHistory() {
        return promptCacheRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * 获取最近的历史记录
     * @param limit 限制数量
     * @return 最近的历史记录
     */
    public List<PromptCache> getRecentHistory(int limit) {
        return promptCacheRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取历史记录详情
     * @param id 历史记录ID
     * @return 历史记录详情
     */
    public Optional<PromptCache> getHistoryById(Long id) {
        return promptCacheRepository.findById(id);
    }
    
    /**
     * 保存历史记录
     * @param promptCache 历史记录
     * @return 保存后的历史记录
     */
    public PromptCache saveHistory(PromptCache promptCache) {
        return promptCacheRepository.save(promptCache);
    }
    
    /**
     * 获取历史记录数量
     * @return 历史记录总数
     */
    public long getHistoryCount() {
        return promptCacheRepository.count();
    }
    
    /**
     * 根据关键词搜索历史记录
     * @param keyword 搜索关键词
     * @return 匹配的历史记录
     */
    public List<PromptCache> searchHistory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHistory();
        }
        return promptCacheRepository.findByTaskDescriptionContaining(keyword.trim());
    }

    // ==================== 新增分类和标签相关方法 ====================

    /**
     * 分页查询历史记录（支持筛选）
     */
    public PagedResult<PromptCache> getHistoryPage(HistoryQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Page<PromptCache> page = promptCacheRepository.findByFilters(
                request.getCategoryId(),
                request.getIsFavorite(),
                request.getKeyword(),
                pageable
        );

        return new PagedResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                request.getPage(),
                request.getSize()
        );
    }

    /**
     * 根据分类查询历史记录
     */
    public List<PromptCache> getHistoryByCategory(Long categoryId) {
        return promptCacheRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
    }

    /**
     * 获取收藏的提示词
     */
    public List<PromptCache> getFavoritePrompts() {
        return promptCacheRepository.findByIsFavoriteTrueOrderByCreatedAtDesc();
    }

    /**
     * 分页获取收藏的提示词
     */
    public PagedResult<PromptCache> getFavoritePromptsPage(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PromptCache> result = promptCacheRepository.findByIsFavoriteTrueOrderByCreatedAtDesc(pageable);

        return new PagedResult<>(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        );
    }

    /**
     * 根据标签查询历史记录
     */
    public PagedResult<PromptCache> getHistoryByTags(Set<Long> tagIds, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PromptCache> result = promptCacheRepository.findByTagIds(tagIds, pageable);

        return new PagedResult<>(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        );
    }

    /**
     * 切换收藏状态
     */
    @Transactional
    public boolean toggleFavorite(Long id) {
        return promptCacheRepository.findById(id)
                .map(prompt -> {
                    prompt.setIsFavorite(!Boolean.TRUE.equals(prompt.getIsFavorite()));
                    promptCacheRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

    /**
     * 更新提示词分类
     */
    @Transactional
    public boolean updateCategory(Long id, Long categoryId) {
        return promptCacheRepository.findById(id)
                .map(prompt -> {
                    prompt.setCategoryId(categoryId);
                    promptCacheRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

    /**
     * 删除历史记录
     */
    @Transactional
    public boolean deleteHistory(Long id) {
        if (promptCacheRepository.existsById(id)) {
            promptCacheRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 批量删除历史记录
     */
    @Transactional
    public int batchDeleteHistory(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            if (deleteHistory(id)) {
                count++;
            }
        }
        return count;
    }
}