package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;

import java.util.List;

/**
 * 角色定义分析器
 */
public class RoleAnalyzer extends AbstractQualityAnalyzer {
    
    @Override
    public Dimension getDimension() {
        return Dimension.ROLE;
    }
    
    @Override
    public int analyze(String prompt) {
        return calculateKeywordScore(prompt,
            // 中文角色关键词
            new KeywordGroup(new String[]{"你是", "你是一个", "你是一位"}, 20),
            new KeywordGroup(new String[]{"角色", "身份", "扮演"}, 15),
            new KeywordGroup(new String[]{"专家", "助手", "顾问", "工程师", "分析师"}, 10),
            new KeywordGroup(new String[]{"具备", "拥有", "擅长", "精通", "专业"}, 10),
            // 英文角色关键词
            new KeywordGroup(new String[]{"you are", "you act as", "act as"}, 20),
            new KeywordGroup(new String[]{"expert", "assistant", "specialist"}, 10)
        );
    }
    
    @Override
    public void addImprovement(String prompt, int score, List<AnalyzeResponse.Improvement> improvements) {
        if (score < SCORE_THRESHOLD) {
            improvements.add(new AnalyzeResponse.Improvement(
                "角色定义",
                "缺乏清晰的角色定义",
                "建议明确定义AI的角色身份，例如'你是一位专业的...'或'你是一个...'，帮助AI更好地理解期望的能力范围",
                1
            ));
        }
    }
    
    @Override
    public void addStrength(String prompt, int score, List<String> strengths) {
        if (score >= STRENGTH_THRESHOLD) {
            strengths.add("✅ 角色定义清晰");
        }
    }
}