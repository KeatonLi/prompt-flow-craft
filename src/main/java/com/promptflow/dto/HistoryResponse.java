package com.promptflow.dto;

import com.promptflow.entity.PromptTag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    // 扩展字段
    private Long categoryId;
    private CategoryResponse category;
    private Integer likeCount;
    private Boolean isAutoTagged;
    private List<String> aiTags;
    private List<TagResponse> tags;
    private String usageScenario;
    private Integer effectivenessScore;

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

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public CategoryResponse getCategory() { return category; }
    public void setCategory(CategoryResponse category) { this.category = category; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Boolean getIsAutoTagged() { return isAutoTagged; }
    public void setIsAutoTagged(Boolean isAutoTagged) { this.isAutoTagged = isAutoTagged; }

    public List<String> getAiTags() { return aiTags; }
    public void setAiTags(List<String> aiTags) { this.aiTags = aiTags; }

    public List<TagResponse> getTags() { return tags; }
    public void setTags(List<TagResponse> tags) { this.tags = tags; }

    public String getUsageScenario() { return usageScenario; }
    public void setUsageScenario(String usageScenario) { this.usageScenario = usageScenario; }

    public Integer getEffectivenessScore() { return effectivenessScore; }
    public void setEffectivenessScore(Integer effectivenessScore) { this.effectivenessScore = effectivenessScore; }

    /**
     * 内部类：分类响应
     */
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String icon;
        private String color;

        public CategoryResponse() {}

        public CategoryResponse(Long id, String name, String icon, String color) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.color = color;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    /**
     * 内部类：标签响应
     */
    public static class TagResponse {
        private Long id;
        private String name;
        private String color;
        private Integer usageCount;

        public TagResponse() {}

        public TagResponse(Long id, String name, String color, Integer usageCount) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.usageCount = usageCount;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Integer getUsageCount() { return usageCount; }
        public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    }
}