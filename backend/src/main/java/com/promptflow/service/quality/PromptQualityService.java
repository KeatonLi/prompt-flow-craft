package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 提示词质量评估服务
 * 基于规则引擎评估提示词质量
 */
@Service
public class PromptQualityService {
    
    private final List<QualityAnalyzer> analyzers;
    
    public PromptQualityService() {
        // 初始化所有质量分析器
        this.analyzers = List.of(
            new StructureAnalyzer(),
            new RoleAnalyzer(),
            new TaskAnalyzer(),
            new ConstraintAnalyzer(),
            new OutputFormatAnalyzer()
        );
    }
    
    /**
     * 综合评估提示词质量
     * @param prompt 提示词内容
     * @return 质量评分（0-100）
     */
    public int evaluate(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return 0;
        }
        
        // 基础分数
        int baseScore = 50;
        
        // 长度评估
        int lengthScore = evaluateLength(prompt);
        
        // 各维度评估的平均分
        int dimensionScore = (int) analyzers.stream()
            .mapToInt(a -> a.analyze(prompt))
            .average()
            .orElse(0);
        
        int totalScore = baseScore + lengthScore + (dimensionScore / 5);
        return Math.min(totalScore, 100);
    }
    
    /**
     * 详细分析提示词质量
     * @param prompt 提示词内容
     * @return 详细分析报告
     */
    public AnalyzeResponse analyze(String prompt) {
        AnalyzeResponse response = new AnalyzeResponse();
        List<AnalyzeResponse.Improvement> improvements = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        
        // 各维度分析
        int structureScore = 0;
        int roleScore = 0;
        int taskScore = 0;
        int constraintScore = 0;
        int outputScore = 0;
        
        for (QualityAnalyzer analyzer : analyzers) {
            int score = analyzer.analyze(prompt);
            analyzer.addImprovement(prompt, score, improvements);
            analyzer.addStrength(prompt, score, strengths);
            
            switch (analyzer.getDimension()) {
                case STRUCTURE -> structureScore = score;
                case ROLE -> roleScore = score;
                case TASK -> taskScore = score;
                case CONSTRAINT -> constraintScore = score;
                case OUTPUT_FORMAT -> outputScore = score;
            }
        }
        
        response.setStructureScore(structureScore);
        response.setRoleScore(roleScore);
        response.setTaskScore(taskScore);
        response.setConstraintScore(constraintScore);
        response.setOutputScore(outputScore);
        
        // 计算总分
        int totalScore = (structureScore + roleScore + taskScore + constraintScore + outputScore) / 5;
        response.setTotalScore(totalScore);
        response.setScoreLevel(calculateScoreLevel(totalScore));
        
        // 排序和设置改进建议
        improvements.sort(Comparator.comparingInt(AnalyzeResponse.Improvement::getPriority));
        response.setImprovements(improvements);
        response.setStrengths(strengths);
        
        return response;
    }
    
    /**
     * 评估长度
     */
    private int evaluateLength(String prompt) {
        int score = 0;
        if (prompt.length() > 50) score += 5;
        if (prompt.length() > 100) score += 5;
        if (prompt.length() > 200) score += 5;
        return score;
    }
    
    /**
     * 计算评分等级
     */
    private String calculateScoreLevel(int score) {
        if (score >= 90) return "优秀";
        if (score >= 70) return "良好";
        if (score >= 50) return "一般";
        return "较差";
    }
}