package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;

import java.util.List;

/**
 * 任务清晰度分析器
 */
public class TaskAnalyzer extends AbstractQualityAnalyzer {
    
    @Override
    public Dimension getDimension() {
        return Dimension.TASK;
    }
    
    @Override
    public int analyze(String prompt) {
        return calculateKeywordScore(prompt,
            // 中文任务关键词
            new KeywordGroup(new String[]{"请", "帮我", "需要", "希望"}, 15),
            new KeywordGroup(new String[]{"任务", "目标", "目的", "需求"}, 10),
            new KeywordGroup(new String[]{"生成", "创建", "编写", "撰写"}, 10),
            new KeywordGroup(new String[]{"分析", "处理", "计算", "评估"}, 10),
            // 英文任务关键词
            new KeywordGroup(new String[]{"please", "help me", "i need", "i want"}, 15),
            new KeywordGroup(new String[]{"generate", "create", "write", "produce"}, 10)
        );
    }
    
    @Override
    public void addImprovement(String prompt, int score, List<AnalyzeResponse.Improvement> improvements) {
        if (score < SCORE_THRESHOLD) {
            improvements.add(new AnalyzeResponse.Improvement(
                "任务清晰度",
                "任务描述不够清晰",
                "建议更清晰地描述任务要求，使用'请帮我...'、'请生成...'等明确指令，说明具体的任务目标和期望结果",
                1
            ));
        }
    }
    
    @Override
    public void addStrength(String prompt, int score, List<String> strengths) {
        if (score >= STRENGTH_THRESHOLD) {
            strengths.add("✅ 任务描述明确");
        }
    }
}