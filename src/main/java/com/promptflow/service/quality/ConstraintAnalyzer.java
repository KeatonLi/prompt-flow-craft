package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;

import java.util.List;

/**
 * 约束条件分析器
 */
public class ConstraintAnalyzer extends AbstractQualityAnalyzer {
    
    @Override
    public Dimension getDimension() {
        return Dimension.CONSTRAINT;
    }
    
    @Override
    public int analyze(String prompt) {
        return calculateKeywordScore(prompt,
            // 中文约束关键词
            new KeywordGroup(new String[]{"不能", "不要", "避免", "禁止"}, 15),
            new KeywordGroup(new String[]{"必须", "需要", "应该", "务必"}, 10),
            new KeywordGroup(new String[]{"限制", "约束", "范围", "条件"}, 10),
            new KeywordGroup(new String[]{"只", "仅", "不超过", "至少", "最多"}, 10),
            // 英文约束关键词
            new KeywordGroup(new String[]{"don't", "must not", "avoid", "never"}, 15),
            new KeywordGroup(new String[]{"must", "should", "need to", "have to"}, 10),
            new KeywordGroup(new String[]{"limit", "only", "maximum", "minimum", "at most"}, 10)
        );
    }
    
    @Override
    public void addImprovement(String prompt, int score, List<AnalyzeResponse.Improvement> improvements) {
        if (score < SCORE_THRESHOLD) {
            improvements.add(new AnalyzeResponse.Improvement(
                "约束条件",
                "缺少明确的约束条件",
                "建议添加具体的约束条件，如'不要...'、'必须...'、'限制在...'等，明确AI的行为边界",
                2
            ));
        }
    }
    
    @Override
    public void addStrength(String prompt, int score, List<String> strengths) {
        if (score >= STRENGTH_THRESHOLD) {
            strengths.add("✅ 包含约束条件");
        }
    }
}