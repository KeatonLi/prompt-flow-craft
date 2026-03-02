package com.promptflow.dto;

import java.util.List;

public class CompareResponse {
    private String summary;
    private int scoreA;
    private int scoreB;
    private String winner;
    private List<ComparisonItem> comparisons;
    private List<String> suggestions;
    private int structureDiff;
    private int roleDiff;
    private int taskDiff;
    private int constraintDiff;
    private int outputDiff;
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public int getScoreA() {
        return scoreA;
    }
    
    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }
    
    public int getScoreB() {
        return scoreB;
    }
    
    public void setScoreB(int scoreB) {
        this.scoreB = scoreB;
    }
    
    public String getWinner() {
        return winner;
    }
    
    public void setWinner(String winner) {
        this.winner = winner;
    }
    
    public List<ComparisonItem> getComparisons() {
        return comparisons;
    }
    
    public void setComparisons(List<ComparisonItem> comparisons) {
        this.comparisons = comparisons;
    }
    
    public List<String> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
    
    public int getStructureDiff() {
        return structureDiff;
    }
    
    public void setStructureDiff(int structureDiff) {
        this.structureDiff = structureDiff;
    }
    
    public int getRoleDiff() {
        return roleDiff;
    }
    
    public void setRoleDiff(int roleDiff) {
        this.roleDiff = roleDiff;
    }
    
    public int getTaskDiff() {
        return taskDiff;
    }
    
    public void setTaskDiff(int taskDiff) {
        this.taskDiff = taskDiff;
    }
    
    public int getConstraintDiff() {
        return constraintDiff;
    }
    
    public void setConstraintDiff(int constraintDiff) {
        this.constraintDiff = constraintDiff;
    }
    
    public int getOutputDiff() {
        return outputDiff;
    }
    
    public void setOutputDiff(int outputDiff) {
        this.outputDiff = outputDiff;
    }
    
    public static class ComparisonItem {
        private String aspect;
        private int scoreA;
        private int scoreB;
        private String analysis;
        private String winner;
        
        public String getAspect() {
            return aspect;
        }
        
        public void setAspect(String aspect) {
            this.aspect = aspect;
        }
        
        public int getScoreA() {
            return scoreA;
        }
        
        public void setScoreA(int scoreA) {
            this.scoreA = scoreA;
        }
        
        public int getScoreB() {
            return scoreB;
        }
        
        public void setScoreB(int scoreB) {
            this.scoreB = scoreB;
        }
        
        public String getAnalysis() {
            return analysis;
        }
        
        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }
        
        public String getWinner() {
            return winner;
        }
        
        public void setWinner(String winner) {
            this.winner = winner;
        }
    }
}
