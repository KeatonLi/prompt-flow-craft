package com.promptflow.service;

import com.promptflow.entity.PromptCache;
import com.promptflow.repository.PromptCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
}