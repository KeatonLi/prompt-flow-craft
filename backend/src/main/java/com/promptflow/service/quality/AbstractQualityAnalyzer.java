package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;

import java.util.List;
import java.util.Locale;

/**
 * 质量分析器抽象基类
 * 提供通用的分析方法
 */
public abstract class AbstractQualityAnalyzer implements QualityAnalyzer {
    
    protected static final int SCORE_THRESHOLD = 70; // 阈值，低于此值给出改进建议
    protected static final int STRENGTH_THRESHOLD = 80; // 优点阈值，高于此值算作优点
    
    /**
     * 检查关键词存在性（中英文）
     */
    protected boolean containsAny(String prompt, String... keywords) {
        String lowerPrompt = prompt.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (lowerPrompt.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 计算基于关键词的得分
     */
    protected int calculateKeywordScore(String prompt, KeywordGroup... groups) {
        int score = 40; // 基础分
        
        for (KeywordGroup group : groups) {
            if (containsAny(prompt, group.keywords)) {
                score += group.score;
            }
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * 关键词组
     */
    protected record KeywordGroup(String[] keywords, int score) {}
}