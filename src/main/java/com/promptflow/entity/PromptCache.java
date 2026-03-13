package com.promptflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "prompt_cache")
public class PromptCache {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
    
    @Column(name = "generated_prompt", columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String generatedPrompt;
    
    @Column(name = "prompt_summary", length = 500)
    private String promptSummary;
    
    @Column(name = "request_hash", unique = true)
    private String requestHash;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "hit_count")
    private Integer hitCount = 0;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    // 记录最后一次点赞时间（用于防刷）
    @Column(name = "last_like_time")
    private LocalDateTime lastLikeTime;

    // 点赞冷却时间（秒），默认60秒内不能重复点赞
    private static final int LIKE_COOLDOWN_SECONDS = 60;

    @Column(name = "is_auto_tagged")
    private Boolean isAutoTagged = false;

    @Column(name = "ai_tags", columnDefinition = "JSON")
    private String aiTags;

    @Column(name = "usage_scenario", length = 200)
    private String usageScenario;

    @Column(name = "effectiveness_score")
    private Integer effectivenessScore;

    // 用户评分（1-5星）
    @Column(name = "user_rating")
    private Integer userRating;

    // 用户评分评论
    @Column(name = "rating_comment", columnDefinition = "TEXT")
    private String ratingComment;

    // 评分次数
    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    // 平均评分（缓存）
    @Column(name = "average_rating")
    private Double averageRating;

    // 输入 tokens 数量
    @Column(name = "input_tokens")
    private Long inputTokens = 0L;

    // 输出 tokens 数量
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

    public PromptCache() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getGeneratedPrompt() {
        return generatedPrompt;
    }
    
    public void setGeneratedPrompt(String generatedPrompt) {
        this.generatedPrompt = generatedPrompt;
    }
    
    public String getPromptSummary() {
        return promptSummary;
    }
    
    public void setPromptSummary(String promptSummary) {
        this.promptSummary = promptSummary;
    }
    
    public String getRequestHash() {
        return requestHash;
    }
    
    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getHitCount() {
        return hitCount;
    }
    
    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }
    
    public void incrementHitCount() {
        this.hitCount = (this.hitCount == null ? 0 : this.hitCount) + 1;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public LocalDateTime getLastLikeTime() {
        return lastLikeTime;
    }

    public void setLastLikeTime(LocalDateTime lastLikeTime) {
        this.lastLikeTime = lastLikeTime;
    }

    public Boolean getIsAutoTagged() {
        return isAutoTagged;
    }

    public void setIsAutoTagged(Boolean isAutoTagged) {
        this.isAutoTagged = isAutoTagged;
    }

    public String getAiTags() {
        return aiTags;
    }

    public void setAiTags(String aiTags) {
        this.aiTags = aiTags;
    }

    public String getUsageScenario() {
        return usageScenario;
    }

    public void setUsageScenario(String usageScenario) {
        this.usageScenario = usageScenario;
    }

    public Integer getEffectivenessScore() {
        return effectivenessScore;
    }

    public void setEffectivenessScore(Integer effectivenessScore) {
        this.effectivenessScore = effectivenessScore;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public String getRatingComment() {
        return ratingComment;
    }

    public void setRatingComment(String ratingComment) {
        this.ratingComment = ratingComment;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(Long inputTokens) {
        this.inputTokens = inputTokens;
    }

    public Long getOutputTokens() {
        return outputTokens;
    }

    public void setOutputTokens(Long outputTokens) {
        this.outputTokens = outputTokens;
    }

    // 便捷方法：获取总 tokens
    public Long getTotalTokens() {
        return (inputTokens != null ? inputTokens : 0L) + (outputTokens != null ? outputTokens : 0L);
    }

    public PromptCategory getCategory() {
        return category;
    }

    public void setCategory(PromptCategory category) {
        this.category = category;
    }

    public Set<PromptTag> getTags() {
        return tags;
    }

    public void setTags(Set<PromptTag> tags) {
        this.tags = tags;
    }

    // 便捷方法：添加标签
    public void addTag(PromptTag tag) {
        this.tags.add(tag);
    }

    // 便捷方法：移除标签
    public void removeTag(PromptTag tag) {
        this.tags.remove(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromptCache that = (PromptCache) o;
        return Objects.equals(requestHash, that.requestHash);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(requestHash);
    }
    
    @Override
    public String toString() {
        return "PromptCache{" +
                "id=" + id +
                ", taskDescription='" + taskDescription + '\'' +
                ", requestHash='" + requestHash + '\'' +
                ", hitCount=" + hitCount +
                ", createdAt=" + createdAt +
                "}";
    }
}