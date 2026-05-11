package com.promptflow.service;

import com.promptflow.dto.PromptRequest;
import com.promptflow.entity.PromptResource;
import com.promptflow.repository.PromptResourceRepository;
import com.promptflow.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PromptResourceService {

    private static final Logger logger = LoggerFactory.getLogger(PromptResourceService.class);

    @Autowired
    private PromptResourceRepository promptResourceRepository;

    @Autowired
    private PromptClassificationService classificationService;

    public String getCachedPrompt(PromptRequest request) {
        String requestHash = HashUtil.generateRequestHash(request);
        logger.debug("查找缓存，请求哈希: {}", requestHash);

        Optional<PromptResource> cachedResult = promptResourceRepository.findByRequestHash(requestHash);

        if (cachedResult.isPresent()) {
            PromptResource cache = cachedResult.get();
            cache.incrementHitCount();
            promptResourceRepository.save(cache);

            logger.info("缓存命中! 哈希: {}, 命中次数: {}", requestHash, cache.getHitCount());
            return cache.getGeneratedPrompt();
        }

        logger.debug("缓存未命中，哈希: {}", requestHash);
        return null;
    }

    public void saveToCache(PromptRequest request, String generatedPrompt) {
        try {
            String requestHash = HashUtil.generateRequestHash(request);

            if (promptResourceRepository.existsByRequestHash(requestHash)) {
                logger.debug("缓存已存在，跳过保存。哈希: {}", requestHash);
                return;
            }

            PromptResource cache = new PromptResource();
            cache.setTaskDescription(request.getTaskDescription());
            cache.setTargetAudience(request.getTargetAudience());
            cache.setOutputFormat(request.getOutputFormat());
            cache.setConstraints(request.getConstraints());
            cache.setExamples(request.getExamples());
            cache.setTone(request.getTone());
            cache.setLength(request.getLength());
            cache.setGeneratedPrompt(generatedPrompt);
            if (generatedPrompt != null && generatedPrompt.length() > 100) {
                cache.setPromptSummary(generatedPrompt.substring(0, 100) + "...");
            } else {
                cache.setPromptSummary(generatedPrompt);
            }
            cache.setRequestHash(requestHash);
            cache.setHitCount(0);

            PromptResource savedCache = promptResourceRepository.save(cache);
            logger.info("成功保存到缓存，哈希: {}", requestHash);

            try {
                logger.info("开始自动分类和打标签，promptId: {}", savedCache.getId());
                classificationService.autoClassifyAndTag(savedCache);
            } catch (Exception e) {
                logger.error("自动分类失败，不影响主流程", e);
            }

        } catch (Exception e) {
            logger.error("保存缓存时发生错误", e);
        }
    }
}