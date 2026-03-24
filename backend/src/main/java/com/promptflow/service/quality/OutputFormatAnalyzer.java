package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;

import java.util.List;

/**
 * 输出格式分析器
 */
public class OutputFormatAnalyzer extends AbstractQualityAnalyzer {
    
    @Override
    public Dimension getDimension() {
        return Dimension.OUTPUT_FORMAT;
    }
    
    @Override
    public int analyze(String prompt) {
        return calculateKeywordScore(prompt,
            // 中文格式关键词
            new KeywordGroup(new String[]{"格式", "输出", "返回"}, 15),
            new KeywordGroup(new String[]{"json", "markdown", "md", "xml", "yaml", "表格", "列表"}, 15),
            new KeywordGroup(new String[]{"包含", "包括", "字段", "属性", "键"}, 10),
            // 英文格式关键词
            new KeywordGroup(new String[]{"format", "output", "return"}, 15),
            new KeywordGroup(new String[]{"json", "markdown", "md", "xml", "yaml"}, 15),
            new KeywordGroup(new String[]{"list", "table", "array", "include"}, 10)
        );
    }
    
    @Override
    public void addImprovement(String prompt, int score, List<AnalyzeResponse.Improvement> improvements) {
        if (score < SCORE_THRESHOLD) {
            improvements.add(new AnalyzeResponse.Improvement(
                "输出格式",
                "输出格式要求不明确",
                "建议明确指定输出格式，如JSON、Markdown、列表等格式要求，说明期望包含的字段或结构",
                1
            ));
        }
    }
    
    @Override
    public void addStrength(String prompt, int score, List<String> strengths) {
        if (score >= STRENGTH_THRESHOLD) {
            strengths.add("✅ 输出格式规范");
        }
    }
}