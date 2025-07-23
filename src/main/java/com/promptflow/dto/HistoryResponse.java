package com.promptflow.dto;

import java.time.LocalDateTime;

public class HistoryResponse {
    private Long id;
    private String taskDescription;
    private String targetAudience;
    private String outputFormat;
    private String constraints;
    private String examples;
    private String tone;
    private String length;
    private String generatedPrompt;
    private LocalDateTime createdAt;
    private Integer hitCount;

    public HistoryResponse() {}

    public HistoryResponse(Long id, String taskDescription, String targetAudience, String outputFormat, 
                         String constraints, String examples, String tone, String length, 
                         String generatedPrompt, LocalDateTime createdAt, Integer hitCount) {
        this.id = id;
        this.taskDescription = taskDescription;
        this.targetAudience = targetAudience;
        this.outputFormat = outputFormat;
        this.constraints = constraints;
        this.examples = examples;
        this.tone = tone;
        this.length = length;
        this.generatedPrompt = generatedPrompt;
        this.createdAt = createdAt;
        this.hitCount = hitCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public String getExamples() { return examples; }
    public void setExamples(String examples) { this.examples = examples; }

    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }

    public String getLength() { return length; }
    public void setLength(String length) { this.length = length; }

    public String getGeneratedPrompt() { return generatedPrompt; }
    public void setGeneratedPrompt(String generatedPrompt) { this.generatedPrompt = generatedPrompt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getHitCount() { return hitCount; }
    public void setHitCount(Integer hitCount) { this.hitCount = hitCount; }
}