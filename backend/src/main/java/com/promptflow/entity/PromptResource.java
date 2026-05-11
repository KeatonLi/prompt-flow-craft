package com.promptflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "prompt_resource")
public class PromptResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 提示词类型：agent 或 skill
    @Column(name = "prompt_type", length = 20)
    private String promptType;

    // 名称（Agent名称或Skill名称）
    @Column(name = "name", length = 100)
    private String name;

    // ========== Agent 特有字段 ==========
    @Column(name = "role_description", columnDefinition = "TEXT")
    private String roleDescription;

    @Column(name = "capabilities", columnDefinition = "TEXT")
    private String capabilities;

    @Column(name = "behaviors", columnDefinition = "TEXT")
    private String behaviors;

    @Column(name = "communication_style", length = 50)
    private String communicationStyle;

    // ========== Skill 特有字段 ==========
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "skill_type", length = 20)
    private String skillType;

    @Column(name = "method", length = 10)
    private String method;

    @Column(name = "endpoint", columnDefinition = "TEXT")
    private String endpoint;

    @Column(name = "parameters", columnDefinition = "JSON")
    private String parameters;

    @Column(name = "output_description", columnDefinition = "TEXT")
    private String outputDescription;

    // ========== 通用字段 ==========
    @Column(name = "generated_prompt", columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String generatedPrompt;

    @Column(name = "prompt_summary", length = 500)
    private String promptSummary;

    // ========== 旧泛化提示词字段（用于向后兼容） ==========
    @Column(name = "task_description", columnDefinition = "TEXT")
    private String taskDescription;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "output_format")
    private String outputFormat;

    @Column(name = "constraints", columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "examples", columnDefinition = "TEXT")
    private String examples;

    @Column(name = "tone")
    private String tone;

    @Column(name = "length")
    private String length;

    @Column(name = "request_hash", unique = true)
    private String requestHash;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "author_nickname", length = 100)
    private String authorNickname;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "hit_count")
    private Integer hitCount = 0;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "last_like_time")
    private LocalDateTime lastLikeTime;

    @Column(name = "is_auto_tagged")
    private Boolean isAutoTagged = false;

    @Column(name = "ai_tags", columnDefinition = "JSON")
    private String aiTags;

    @Column(name = "usage_scenario", length = 200)
    private String usageScenario;

    @Column(name = "effectiveness_score")
    private Integer effectivenessScore;

    @Column(name = "user_rating")
    private Integer userRating;

    @Column(name = "rating_comment", columnDefinition = "TEXT")
    private String ratingComment;

    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "input_tokens")
    private Long inputTokens = 0L;

    @Column(name = "output_tokens")
    private Long outputTokens = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private PromptCategory category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "prompt_tag_relation",
        joinColumns = @JoinColumn(name = "prompt_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<PromptTag> tags = new HashSet<>();

    public PromptResource() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.likeCount = 0;
        this.viewCount = 0;
        this.hitCount = 0;
        this.isAutoTagged = false;
        this.ratingCount = 0;
        this.inputTokens = 0L;
        this.outputTokens = 0L;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getHitCount() { return hitCount; }
    public void setHitCount(Integer hitCount) { this.hitCount = hitCount; }

    public void incrementHitCount() {
        this.hitCount = (this.hitCount == null ? 0 : this.hitCount) + 1;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public LocalDateTime getLastLikeTime() { return lastLikeTime; }
    public void setLastLikeTime(LocalDateTime lastLikeTime) { this.lastLikeTime = lastLikeTime; }

    public Boolean getIsAutoTagged() { return isAutoTagged; }
    public void setIsAutoTagged(Boolean isAutoTagged) { this.isAutoTagged = isAutoTagged; }

    public String getAiTags() { return aiTags; }
    public void setAiTags(String aiTags) { this.aiTags = aiTags; }

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

    public Long getInputTokens() { return inputTokens; }
    public void setInputTokens(Long inputTokens) { this.inputTokens = inputTokens; }

    public Long getOutputTokens() { return outputTokens; }
    public void setOutputTokens(Long outputTokens) { this.outputTokens = outputTokens; }

    public Long getTotalTokens() {
        return (inputTokens != null ? inputTokens : 0L) + (outputTokens != null ? outputTokens : 0L);
    }

    public PromptCategory getCategory() { return category; }
    public void setCategory(PromptCategory category) { this.category = category; }

    public Set<PromptTag> getTags() { return tags; }
    public void setTags(Set<PromptTag> tags) { this.tags = tags; }

    public void addTag(PromptTag tag) { this.tags.add(tag); }
    public void removeTag(PromptTag tag) { this.tags.remove(tag); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromptResource that = (PromptResource) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PromptResource{" +
                "id=" + id +
                ", promptType='" + promptType + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                "}";
    }
}
