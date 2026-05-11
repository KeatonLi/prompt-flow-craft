package com.promptflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HistoryResponse {
    private Long id;

    // 统一类型
    private String promptType;
    private String name;

    // Agent 特有
    private String roleDescription;
    private String capabilities;
    private String behaviors;
    private String communicationStyle;

    // Skill 特有
    private String description;
    private String skillType;
    private String method;
    private String endpoint;
    private String parameters;
    private String outputDescription;

    // 通用
    private String generatedPrompt;
    private String promptSummary;
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer viewCount;

    // 扩展字段
    private Long categoryId;
    private CategoryResponse category;
    private Boolean isAutoTagged;
    private List<String> aiTags;
    private List<TagResponse> tags;
    private String usageScenario;
    private Integer effectivenessScore;

    // 用户评分相关
    private Integer userRating;
    private String ratingComment;
    private Integer ratingCount;
    private Double averageRating;

    public HistoryResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPromptType() { return promptType; }
    public void setPromptType(String promptType) { this.promptType = promptType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRoleDescription() { return roleDescription; }
    public void setRoleDescription(String roleDescription) { this.roleDescription = roleDescription; }

    public String getCapabilities() { return capabilities; }
    public void setCapabilities(String capabilities) { this.capabilities = capabilities; }

    public String getBehaviors() { return behaviors; }
    public void setBehaviors(String behaviors) { this.behaviors = behaviors; }

    public String getCommunicationStyle() { return communicationStyle; }
    public void setCommunicationStyle(String communicationStyle) { this.communicationStyle = communicationStyle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSkillType() { return skillType; }
    public void setSkillType(String skillType) { this.skillType = skillType; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public String getOutputDescription() { return outputDescription; }
    public void setOutputDescription(String outputDescription) { this.outputDescription = outputDescription; }

    public String getGeneratedPrompt() { return generatedPrompt; }
    public void setGeneratedPrompt(String generatedPrompt) { this.generatedPrompt = generatedPrompt; }

    public String getPromptSummary() { return promptSummary; }
    public void setPromptSummary(String promptSummary) { this.promptSummary = promptSummary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public CategoryResponse getCategory() { return category; }
    public void setCategory(CategoryResponse category) { this.category = category; }

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

    public Integer getUserRating() { return userRating; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }

    public String getRatingComment() { return ratingComment; }
    public void setRatingComment(String ratingComment) { this.ratingComment = ratingComment; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

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
