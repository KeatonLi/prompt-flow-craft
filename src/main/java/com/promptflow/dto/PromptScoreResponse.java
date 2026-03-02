package com.promptflow.dto;

import java.util.List;

public class PromptScoreResponse {
    
    private int totalScore; // 总分 0-100
    
    private int clarityScore; // 清晰度得分
    
    private int completenessScore; // 完整性得分
    
    private int structureScore; // 结构得分
    
    private int examplesScore; // 示例得分
    
    private String level; // 等级：优秀/良好/一般/待改进
    
    private List<String> strengths; // 优点
    
    private List<String> weaknesses; // 不足
    
    private List<String> suggestions; // 改进建议
    
    private String summary; // 总结
    
    public PromptScoreResponse() {}
    
    // Getters and Setters
    public int getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
    
    public int getClarityScore() {
        return clarityScore;
    }
    
    public void setClarityScore(int clarityScore) {
        this.clarityScore = clarityScore;
    }
    
    public int getCompletenessScore() {
        return completenessScore;
    }
    
    public void setCompletenessScore(int completenessScore) {
        this.completenessScore = completenessScore;
    }
    
    public int getStructureScore() {
        return structureScore;
    }
    
    public void setStructureScore(int structureScore) {
        this.structureScore = structureScore;
    }
    
    public int getExamplesScore() {
        return examplesScore;
    }
    
    public void setExamplesScore(int examplesScore) {
        this.examplesScore = examplesScore;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public List<String> getStrengths() {
        return strengths;
    }
    
    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }
    
    public List<String> getWeaknesses() {
        return weaknesses;
    }
    
    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }
    
    public List<String> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
}
