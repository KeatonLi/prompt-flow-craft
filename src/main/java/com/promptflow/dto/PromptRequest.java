package com.promptflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PromptRequest {
    
    @JsonProperty("task_description")
    private String taskDescription;
    
    @JsonProperty("target_audience")
    private String targetAudience;
    
    @JsonProperty("output_format")
    private String outputFormat;
    
    @JsonProperty("constraints")
    private String constraints;
    
    @JsonProperty("examples")
    private String examples;
    
    @JsonProperty("tone")
    private String tone;
    
    @JsonProperty("length")
    private String length;
    
    public PromptRequest() {}
    
    public PromptRequest(String taskDescription, String targetAudience, String outputFormat, 
                        String constraints, String examples, String tone, String length) {
        this.taskDescription = taskDescription;
        this.targetAudience = targetAudience;
        this.outputFormat = outputFormat;
        this.constraints = constraints;
        this.examples = examples;
        this.tone = tone;
        this.length = length;
    }
    
    // Getters and Setters
    public String getTaskDescription() {
        return taskDescription;
    }
    
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    
    public String getTargetAudience() {
        return targetAudience;
    }
    
    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }
    
    public String getOutputFormat() {
        return outputFormat;
    }
    
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
    
    public String getConstraints() {
        return constraints;
    }
    
    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }
    
    public String getExamples() {
        return examples;
    }
    
    public void setExamples(String examples) {
        this.examples = examples;
    }
    
    public String getTone() {
        return tone;
    }
    
    public void setTone(String tone) {
        this.tone = tone;
    }
    
    public String getLength() {
        return length;
    }
    
    public void setLength(String length) {
        this.length = length;
    }
    
    @Override
    public String toString() {
        return "PromptRequest{" +
                "taskDescription='" + taskDescription + '\'' +
                ", targetAudience='" + targetAudience + '\'' +
                ", outputFormat='" + outputFormat + '\'' +
                ", constraints='" + constraints + '\'' +
                ", examples='" + examples + '\'' +
                ", tone='" + tone + '\'' +
                ", length='" + length + '\'' +
                '}';
    }
}