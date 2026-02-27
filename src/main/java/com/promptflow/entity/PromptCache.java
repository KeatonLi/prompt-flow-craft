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
    private String generatedPrompt;
    
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

    @Column(name = "is_auto_tagged")
    private Boolean isAutoTagged = false;

    @Column(name = "ai_tags", columnDefinition = "JSON")
    private String aiTags;

    @Column(name = "usage_scenario", length = 200)
    private String usageScenario;

    @Column(name = "effectiveness_score")
    private Integer effectivenessScore;

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