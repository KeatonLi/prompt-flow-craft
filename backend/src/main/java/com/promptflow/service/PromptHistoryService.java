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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromptHistoryService {

    @Autowired
    private PromptCacheRepository promptCacheRepository;

    public List<PromptCache> getAllHistory() {
        return promptCacheRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<PromptCache> getRecentHistory(int limit) {
        List<Object[]> results = promptCacheRepository.findRecentHistorySummary(PageRequest.of(0, limit));
        return results.stream()
            .map(this::convertToPromptCache)
            .collect(Collectors.toList());
    }

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

    public Optional<PromptCache> getHistoryById(Long id) {
        return promptCacheRepository.findById(id);
    }

    public PromptCache saveHistory(PromptCache promptCache) {
        return promptCacheRepository.save(promptCache);
    }

    public long getHistoryCount() {
        return promptCacheRepository.count();
    }

    public List<PromptCache> searchHistory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHistory();
        }
        return promptCacheRepository.findByTaskDescriptionContaining(keyword.trim());
    }

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
                request.getSortBy(),
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

    public List<PromptCache> getHistoryByCategory(Long categoryId) {
        return promptCacheRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
    }

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

    @Transactional
    public boolean likePrompt(Long id) {
        return promptCacheRepository.findById(id)
                .map(prompt -> {
                    Integer currentCount = prompt.getLikeCount();
                    prompt.setLikeCount(currentCount == null ? 1 : currentCount + 1);
                    promptCacheRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

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

    @Transactional
    public boolean deleteHistory(Long id) {
        if (promptCacheRepository.existsById(id)) {
            promptCacheRepository.deleteById(id);
            return true;
        }
        return false;
    }

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

    @Transactional
    public boolean ratePrompt(Long id, Integer rating, String comment) {
        return promptCacheRepository.findById(id)
                .map(prompt -> {
                    prompt.setUserRating(rating);
                    prompt.setRatingComment(comment);

                    Integer currentCount = prompt.getRatingCount();
                    prompt.setRatingCount(currentCount == null ? 1 : currentCount + 1);

                    Double currentAvg = prompt.getAverageRating();
                    if (currentAvg == null) {
                        prompt.setAverageRating(rating.doubleValue());
                    } else {
                        double newAvg = (currentAvg * currentCount + rating) / (currentCount + 1);
                        prompt.setAverageRating(newAvg);
                    }

                    promptCacheRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

    public PagedResult<PromptCache> getTopRatedPrompts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PromptCache> result = promptCacheRepository.findByAverageRatingIsNotNullOrderByAverageRatingDesc(pageable);

        return new PagedResult<>(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        );
    }
}
