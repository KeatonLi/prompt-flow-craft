package com.promptflow.dto.llm;

import java.util.List;

/**
 * LLM 响应封装
 */
public class LLMResponse {
    
    private String id;
    private String content;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private String model;
    private Long created;
    private List<Choice> choices;
    private ErrorInfo error;
    
    public LLMResponse() {}
    
    public static LLMResponse success(String content) {
        LLMResponse response = new LLMResponse();
        response.setContent(content);
        return response;
    }
    
    public static LLMResponse error(String message) {
        LLMResponse response = new LLMResponse();
        response.setError(new ErrorInfo(message));
        return response;
    }
    
    public boolean isSuccess() {
        return error == null && content != null;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Integer getPromptTokens() { return promptTokens; }
    public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
    
    public Integer getCompletionTokens() { return completionTokens; }
    public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
    
    public Integer getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Long getCreated() { return created; }
    public void setCreated(Long created) { this.created = created; }
    
    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }
    
    public ErrorInfo getError() { return error; }
    public void setError(ErrorInfo error) { this.error = error; }
    
    /**
     * 选择项
     */
    public static class Choice {
        private Integer index;
        private Message message;
        private String finishReason;
        
        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }
        
        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
        
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }
    
    /**
     * 消息
     */
    public static class Message {
        private String role;
        private String content;
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    /**
     * 错误信息
     */
    public static class ErrorInfo {
        private String message;
        private String type;
        private String code;
        
        public ErrorInfo() {}
        
        public ErrorInfo(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }
}