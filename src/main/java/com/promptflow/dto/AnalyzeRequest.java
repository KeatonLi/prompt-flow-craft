package com.promptflow.dto;

public class AnalyzeRequest {
    
    private String prompt;
    
    public AnalyzeRequest() {}
    
    public AnalyzeRequest(String prompt) {
        this.prompt = prompt;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
