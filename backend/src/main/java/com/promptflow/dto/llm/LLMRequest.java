package com.promptflow.dto.llm;

import java.util.List;
import java.util.Map;

/**
 * LLM 请求封装
 */
public class LLMRequest {
    
    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer maxTokens;
    private Boolean stream;
    private Map<String, Object> extraParams;
    
    public LLMRequest() {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    
    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }
    
    public Map<String, Object> getExtraParams() { return extraParams; }
    public void setExtraParams(Map<String, Object> extraParams) { this.extraParams = extraParams; }
    
    /**
     * 消息对象
     */
    public static class Message {
        private String role;
        private String content;
        
        public Message() {}
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public static Message system(String content) {
            return new Message("system", content);
        }
        
        public static Message user(String content) {
            return new Message("user", content);
        }
        
        public static Message assistant(String content) {
            return new Message("assistant", content);
        }
    }
    
    /**
     * Builder 模式
     */
    public static class Builder {
        private LLMRequest request = new LLMRequest();
        
        public Builder model(String model) {
            request.setModel(model);
            return this;
        }
        
        public Builder messages(List<Message> messages) {
            request.setMessages(messages);
            return this;
        }
        
        public Builder temperature(Double temperature) {
            request.setTemperature(temperature);
            return this;
        }
        
        public Builder maxTokens(Integer maxTokens) {
            request.setMaxTokens(maxTokens);
            return this;
        }
        
        public Builder stream(Boolean stream) {
            request.setStream(stream);
            return this;
        }
        
        public Builder extraParams(Map<String, Object> extraParams) {
            request.setExtraParams(extraParams);
            return this;
        }
        
        public LLMRequest build() {
            return request;
        }
    }
}