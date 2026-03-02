package com.promptflow.dto;

public class PromptScoreRequest {
    
    private String prompt;
    private String category; // 可选：提示词类型
    
    public PromptScoreRequest() {}
    
    public PromptScoreRequest(String prompt) {
        this.prompt = prompt;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}
