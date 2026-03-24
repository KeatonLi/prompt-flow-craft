package com.promptflow.dto;

import java.util.List;

public class OptimizeResponse {
    
    private String optimizedPrompt;
    private String explanation;
    private List<Improvement> improvements;
    private int scoreBefore;
    private int scoreAfter;
    
    public OptimizeResponse() {}
    
    public OptimizeResponse(String optimizedPrompt, String explanation, 
                           List<Improvement> improvements, int scoreBefore, int scoreAfter) {
        this.optimizedPrompt = optimizedPrompt;
        this.explanation = explanation;
        this.improvements = improvements;
        this.scoreBefore = scoreBefore;
        this.scoreAfter = scoreAfter;
    }
    
    public String getOptimizedPrompt() {
        return optimizedPrompt;
    }
    
    public void setOptimizedPrompt(String optimizedPrompt) {
        this.optimizedPrompt = optimizedPrompt;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public List<Improvement> getImprovements() {
        return improvements;
    }
    
    public void setImprovements(List<Improvement> improvements) {
        this.improvements = improvements;
    }
    
    public int getScoreBefore() {
        return scoreBefore;
    }
    
    public void setScoreBefore(int scoreBefore) {
        this.scoreBefore = scoreBefore;
    }
    
    public int getScoreAfter() {
        return scoreAfter;
    }
    
    public void setScoreAfter(int scoreAfter) {
        this.scoreAfter = scoreAfter;
    }
    
    public static class Improvement {
        private String type;
        private String description;
        private String suggestion;
        
        public Improvement() {}
        
        public Improvement(String type, String description, String suggestion) {
            this.type = type;
            this.description = description;
            this.suggestion = suggestion;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getSuggestion() {
            return suggestion;
        }
        
        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }
}
