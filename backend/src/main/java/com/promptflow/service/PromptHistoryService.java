package com.promptflow.service;

import com.promptflow.dto.HistoryQueryRequest;
import com.promptflow.dto.PagedResult;
import com.promptflow.entity.PromptResource;
import com.promptflow.repository.PromptResourceRepository;
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
    private PromptResourceRepository promptResourceRepository;

    public List<PromptResource> getAllHistory() {
        return promptResourceRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<PromptResource> getRecentHistory(int limit) {
        List<Object[]> results = promptResourceRepository.findRecentHistorySummary(PageRequest.of(0, limit));
        return results.stream()
            .map(this::convertToPromptResource)
            .collect(Collectors.toList());
    }

    private PromptResource convertToPromptResource(Object[] row) {
        PromptResource p = new PromptResource();
        p.setId((Long) row[0]);
        p.setName((String) row[1]);
        p.setPromptSummary((String) row[2]);
        p.setCreatedAt((LocalDateTime) row[3]);
        p.setHitCount((Integer) row[4]);
        p.setCategoryId((Long) row[5]);
        p.setLikeCount((Integer) row[6]);
        p.setIsAutoTagged((Boolean) row[7]);
        p.setUsageScenario((String) row[8]);
        p.setEffectivenessScore((Integer) row[9]);
        p.setAiTags((String) row[10]);
        p.setPromptType((String) row[11]);
        return p;
    }

    public Optional<PromptResource> getHistoryById(Long id) {
        return promptResourceRepository.findById(id);
    }

    public PromptResource saveHistory(PromptResource promptCache) {
        return promptResourceRepository.save(promptCache);
    }

    public long getHistoryCount() {
        return promptResourceRepository.count();
    }

    public List<PromptResource> searchHistory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHistory();
        }
        return promptResourceRepository.findByNameContaining(keyword.trim());
    }

    public PagedResult<PromptResource> getHistoryPage(HistoryQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        String sortBy = request.getSortBy();
        Page<PromptResource> page;

        if ("likeCount".equals(sortBy)) {
            page = promptResourceRepository.findAllOrderByLikeCountDesc(pageable);
        } else {
            page = promptResourceRepository.findByFilters(
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

    public List<PromptResource> getHistoryByCategory(Long categoryId) {
        return promptResourceRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
    }

    public PagedResult<PromptResource> getTopLikedPromptsPage(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PromptResource> result = promptResourceRepository.findByLikeCountGreaterThanZeroOrderByLikeCountDesc(pageable);

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
        return promptResourceRepository.findById(id)
                .map(prompt -> {
                    Integer currentCount = prompt.getLikeCount();
                    prompt.setLikeCount(currentCount == null ? 1 : currentCount + 1);
                    promptResourceRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean unlikePrompt(Long id) {
        return promptResourceRepository.findById(id)
                .map(prompt -> {
                    Integer currentCount = prompt.getLikeCount();
                    if (currentCount != null && currentCount > 0) {
                        prompt.setLikeCount(currentCount - 1);
                        promptResourceRepository.save(prompt);
                    }
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean updateCategory(Long id, Long categoryId) {
        return promptResourceRepository.findById(id)
                .map(prompt -> {
                    prompt.setCategoryId(categoryId);
                    promptResourceRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean deleteHistory(Long id) {
        if (promptResourceRepository.existsById(id)) {
            promptResourceRepository.deleteById(id);
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
        return promptResourceRepository.findById(id)
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

                    promptResourceRepository.save(prompt);
                    return true;
                })
                .orElse(false);
    }

    public PagedResult<PromptResource> getTopRatedPrompts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PromptResource> result = promptResourceRepository.findByAverageRatingIsNotNullOrderByAverageRatingDesc(pageable);

        return new PagedResult<>(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        );
    }
}
