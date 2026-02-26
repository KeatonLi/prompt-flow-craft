package com.promptflow.service;

import com.promptflow.dto.PromptRequest;
import com.promptflow.entity.PromptCache;
import com.promptflow.repository.PromptCacheRepository;
import com.promptflow.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PromptCacheService {

    private static final Logger logger = LoggerFactory.getLogger(PromptCacheService.class);

    @Autowired
    private PromptCacheRepository promptCacheRepository;

    @Autowired
    private PromptClassificationService classificationService;
    
    /**
     * 根据请求参数查找缓存的结果
     * @param request 提示词请求
     * @return 缓存的生成结果，如果没有缓存则返回null
     */
    public String getCachedPrompt(PromptRequest request) {
        String requestHash = HashUtil.generateRequestHash(request);
        logger.debug("查找缓存，请求哈希: {}", requestHash);
        
        Optional<PromptCache> cachedResult = promptCacheRepository.findByRequestHash(requestHash);
        
        if (cachedResult.isPresent()) {
            PromptCache cache = cachedResult.get();
            // 增加命中次数
            cache.incrementHitCount();
            promptCacheRepository.save(cache);
            
            logger.info("缓存命中! 哈希: {}, 命中次数: {}", requestHash, cache.getHitCount());
            return cache.getGeneratedPrompt();
        }
        
        logger.debug("缓存未命中，哈希: {}", requestHash);
        return null;
    }
    
    /**
     * 保存生成的提示词到缓存
     * @param request 原始请求
     * @param generatedPrompt 生成的提示词
     */
    public void saveToCache(PromptRequest request, String generatedPrompt) {
        try {
            String requestHash = HashUtil.generateRequestHash(request);
            
            // 检查是否已存在（避免重复保存）
            if (promptCacheRepository.existsByRequestHash(requestHash)) {
                logger.debug("缓存已存在，跳过保存。哈希: {}", requestHash);
                return;
            }
            
            PromptCache cache = new PromptCache();
            cache.setTaskDescription(request.getTaskDescription());
            cache.setTargetAudience(request.getTargetAudience());
            cache.setOutputFormat(request.getOutputFormat());
            cache.setConstraints(request.getConstraints());
            cache.setExamples(request.getExamples());
            cache.setTone(request.getTone());
            cache.setLength(request.getLength());
            cache.setGeneratedPrompt(generatedPrompt);
            cache.setRequestHash(requestHash);
            cache.setHitCount(0);
            
            PromptCache savedCache = promptCacheRepository.save(cache);
            logger.info("成功保存到缓存，哈希: {}", requestHash);

            // 异步执行自动分类和打标签
            try {
                logger.info("开始自动分类和打标签，promptId: {}", savedCache.getId());
                classificationService.autoClassifyAndTag(savedCache);
            } catch (Exception e) {
                logger.error("自动分类失败，不影响主流程", e);
            }

        } catch (Exception e) {
            logger.error("保存缓存时发生错误", e);
            // 缓存保存失败不应该影响主流程，所以只记录日志
        }
    }
    
    /**
     * 检查请求是否已缓存
     * @param request 提示词请求
     * @return 是否已缓存
     */
    public boolean isCached(PromptRequest request) {
        String requestHash = HashUtil.generateRequestHash(request);
        return promptCacheRepository.existsByRequestHash(requestHash);
    }
    
    /**
     * 获取缓存统计信息
     * @return 缓存统计
     */
    public CacheStats getCacheStats() {
        Long totalCacheCount = promptCacheRepository.getTotalCacheCount();
        Long totalHitCount = promptCacheRepository.getTotalHitCount();
        
        return new CacheStats(
            totalCacheCount != null ? totalCacheCount : 0L,
            totalHitCount != null ? totalHitCount : 0L
        );
    }
    
    /**
     * 缓存统计信息类
     */
    public static class CacheStats {
        private final Long totalCacheCount;
        private final Long totalHitCount;
        
        public CacheStats(Long totalCacheCount, Long totalHitCount) {
            this.totalCacheCount = totalCacheCount;
            this.totalHitCount = totalHitCount;
        }
        
        public Long getTotalCacheCount() {
            return totalCacheCount;
        }
        
        public Long getTotalHitCount() {
            return totalHitCount;
        }
        
        public double getHitRate() {
            if (totalCacheCount == 0) {
                return 0.0;
            }
            return (double) totalHitCount / totalCacheCount;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{totalCache=%d, totalHits=%d, hitRate=%.2f%%}", 
                totalCacheCount, totalHitCount, getHitRate() * 100);
        }
    }
}