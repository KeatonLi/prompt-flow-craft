package com.promptflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PromptResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("generated_prompt")
    private String generatedPrompt;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    public PromptResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public PromptResponse(boolean success, String generatedPrompt, String message) {
        this.success = success;
        this.generatedPrompt = generatedPrompt;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static PromptResponse success(String generatedPrompt) {
        return new PromptResponse(true, generatedPrompt, "提示词生成成功");
    }
    
    public static PromptResponse error(String message) {
        return new PromptResponse(false, null, message);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getGeneratedPrompt() {
        return generatedPrompt;
    }
    
    public void setGeneratedPrompt(String generatedPrompt) {
        this.generatedPrompt = generatedPrompt;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "PromptResponse{" +
                "success=" + success +
                ", generatedPrompt='" + generatedPrompt + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}