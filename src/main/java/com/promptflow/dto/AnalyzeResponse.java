package com.promptflow.dto;

import java.util.List;

public class AnalyzeResponse {
    
    private int totalScore;
    private String scoreLevel; // "优秀", "良好", "一般", "较差"
    private int structureScore;
    private int roleScore;
    private int taskScore;
    private int constraintScore;
    private int outputScore;
    private List<Improvement> improvements;
    private List<String> strengths;
    
    public static class Improvement {
        private String category;
        private String suggestion;
        private int priority; // 1-3, 1是最高优先级
        
        public Improvement() {}
        
        public Improvement(String category, String suggestion, int priority) {
            this.category = category;
            this.suggestion = suggestion;
            this.priority = priority;
        }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
    
    public AnalyzeResponse() {}
    
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public String getScoreLevel() { return scoreLevel; }
    public void setScoreLevel(String scoreLevel) { this.scoreLevel = scoreLevel; }
    public int getStructureScore() { return structureScore; }
    public void setStructureScore(int structureScore) { this.structureScore = structureScore; }
    public int getRoleScore() { return roleScore; }
    public void setRoleScore(int roleScore) { this.roleScore = roleScore; }
    public int getTaskScore() { return taskScore; }
    public void setTaskScore(int taskScore) { this.taskScore = taskScore; }
    public int getConstraintScore() { return constraintScore; }
    public void setConstraintScore(int constraintScore) { this.constraintScore = constraintScore; }
    public int getOutputScore() { return outputScore; }
    public void setOutputScore(int outputScore) { this.outputScore = outputScore; }
    public List<Improvement> getImprovements() { return improvements; }
    public void setImprovements(List<Improvement> improvements) { this.improvements = improvements; }
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }
}
