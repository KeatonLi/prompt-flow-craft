package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;

import java.util.List;

/**
 * 结构完整度分析器
 */
public class StructureAnalyzer extends AbstractQualityAnalyzer {
    
    @Override
    public Dimension getDimension() {
        return Dimension.STRUCTURE;
    }
    
    @Override
    public int analyze(String prompt) {
        int score = 60;
        
        // 长度检查
        if (prompt.length() > 50) score += 5;
        if (prompt.length() > 100) score += 10;
        if (prompt.length() > 300) score += 10;
        if (prompt.length() > 500) score += 5;
        
        // 段落分隔检查
        if (prompt.contains("\n\n") || prompt.contains("。\n")) score += 5;
        
        // 编号列表检查
        if (prompt.matches(".*\\d+[.、].*")) score += 5;
        
        return Math.min(score, 100);
    }
    
    @Override
    public void addImprovement(String prompt, int score, List<AnalyzeResponse.Improvement> improvements) {
        if (score < SCORE_THRESHOLD) {
            improvements.add(new AnalyzeResponse.Improvement(
                "结构完整度",
                "提示词结构不够完整或清晰",
                "建议添加清晰的段落分隔、使用编号列表组织内容，确保信息层次分明",
                2
            ));
        }
    }
    
    @Override
    public void addStrength(String prompt, int score, List<String> strengths) {
        if (score >= STRENGTH_THRESHOLD) {
            strengths.add("✅ 结构层次清晰");
        } else if (prompt.length() > 200) {
            strengths.add("✅ 内容详实");
        }
    }
}