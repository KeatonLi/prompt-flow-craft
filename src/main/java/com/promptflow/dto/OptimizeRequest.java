package com.promptflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptimizeRequest {
    
    @JsonProperty("prompt")
    private String prompt;
    
    @JsonProperty("optimizationType")
    private String optimizationType; // "general", "clarity", "specificity", "examples"
    
    @JsonProperty("targetModel")
    private String targetModel; // "gpt", "claude", "general"
    
    public OptimizeRequest() {}
    
    public OptimizeRequest(String prompt, String optimizationType, String targetModel) {
        this.prompt = prompt;
        this.optimizationType = optimizationType;
        this.targetModel = targetModel;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getOptimizationType() {
        return optimizationType;
    }
    
    public void setOptimizationType(String optimizationType) {
        this.optimizationType = optimizationType;
    }
    
    public String getTargetModel() {
        return targetModel;
    }
    
    public void setTargetModel(String targetModel) {
        this.targetModel = targetModel;
    }
}
