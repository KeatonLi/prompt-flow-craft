package com.promptflow.service.quality;

import com.promptflow.dto.AnalyzeResponse;

import java.util.List;

/**
 * 质量分析器接口
 * 定义提示词各个维度的分析方法
 */
public interface QualityAnalyzer {
    
    /**
     * 分析维度枚举
     */
    enum Dimension {
        STRUCTURE,      // 结构完整度
        ROLE,           // 角色定义
        TASK,           // 任务清晰度
        CONSTRAINT,     // 约束条件
        OUTPUT_FORMAT   // 输出格式
    }
    
    /**
     * 获取分析维度
     * @return 维度类型
     */
    Dimension getDimension();
    
    /**
     * 分析提示词在该维度的得分
     * @param prompt 提示词内容
     * @return 得分（0-100）
     */
    int analyze(String prompt);
    
    /**
     * 添加改进建议
     * @param prompt 提示词内容
     * @param score 当前得分
     * @param improvements 改进建议列表
     */
    void addImprovement(String prompt, int score, List<AnalyzeResponse.Improvement> improvements);
    
    /**
     * 添加优点
     * @param prompt 提示词内容
     * @param score 当前得分
     * @param strengths 优点列表
     */
    void addStrength(String prompt, int score, List<String> strengths);
}