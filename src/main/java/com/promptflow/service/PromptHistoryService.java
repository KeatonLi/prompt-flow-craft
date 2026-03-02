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

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

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
     * 获取最近的历史记录（只查摘要，用于列表展示）
     * @param limit 限制数量
     * @return 最近的历史记录列表
     */
    public List<PromptCache> getRecentHistory(int limit) {
        List<Object[]> results = promptCacheRepository.findRecentHistorySummary(PageRequest.of(0, limit));
        return results.stream()
            .map(this::convertToPromptCache)
            .collect(Collectors.toList());
    }

    /**
     * 将查询结果转换为 PromptCache 对象
     * Object[] 顺序: id, taskDescription, targetAudience, promptSummary, createdAt, hitCount, categoryId, likeCount, isAutoTagged, usageScenario, effectivenessScore, aiTags
     */
    private PromptCache convertToPromptCache(Object[] row) {
        PromptCache p = new PromptCache();
        p.setId((Long) row[0]);
        p.setTaskDescription((String) row[1]);
        p.setTargetAudience((String) row[2]);
        p.setPromptSummary((String) row[3]);
        p.setCreatedAt((LocalDateTime) row[4]);
        p.setHitCount((Integer) row[5]);
        p.setCategoryId((Long) row[6]);
        p.setLikeCount((Integer) row[7]);
        p.setIsAutoTagged((Boolean) row[8]);
        p.setUsageScenario((String) row[9]);
        p.setEffectivenessScore((Integer) row[10]);
        p.setAiTags((String) row[11]);
        return p;
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

    /**
     * 分页查询历史记录（支持筛选和排序）
     */
    public PagedResult<PromptCache> getHistoryPage(HistoryQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        String sortBy = request.getSortBy();
        Page<PromptCache> page;

        if ("likeCount".equals(sortBy)) {
            page = promptCacheRepository.findAllOrderByLikeCountDesc(pageable);
        } else {
            page = promptCacheRepository.findByFilters(
                request.getCategoryId(),
                request.getKeyword(),
                sortBy,
                pageable
            );
        }

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
     * 获取点赞数最高的提示词
     */
    public List<PromptCache> getTopLikedPrompts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return promptCacheRepository.findByLikeCountGreaterThanZeroOrderByLikeCountDesc(pageable)
            .getContent();
    }

    /**
     * 分页获取点赞数最高的提示词
     */
    public PagedResult<PromptCache> getTopLikedPromptsPage(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PromptCache> result = promptCacheRepository.findByLikeCountGreaterThanZeroOrderByLikeCountDesc(pageable);

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
     * 点赞提示词（可多次点赞，但60秒内不能连赞）
     */
    @Transactional
    public boolean likePrompt(Long id) {
        return promptCacheRepository.findById(id)
                .map(prompt -> {
                    // 检查冷却时间
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime lastLikeTime = prompt.getLastLikeTime();
                    if (lastLikeTime != null) {
                        long secondsSinceLastLike = java.time.Duration.between(lastLikeTime, now).getSeconds();
                        if (secondsSinceLastLike < 60) {
                            // 冷却期内，不能点赞
                            return false;
                        }
                    }
                    // 更新点赞数和时间
                    Integer currentCount = prompt.getLikeCount();
                    prompt.setLikeCount(currentCount == null ? 1 : currentCount + 1);
                    prompt.setLastLikeTime(now);
                    promptCacheRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

    /**
     * 取消点赞提示词
     */
    @Transactional
    public boolean unlikePrompt(Long id) {
        return promptCacheRepository.findById(id)
                .map(prompt -> {
                    Integer currentCount = prompt.getLikeCount();
                    if (currentCount != null && currentCount > 0) {
                        prompt.setLikeCount(currentCount - 1);
                        promptCacheRepository.save(prompt);
                    }
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
