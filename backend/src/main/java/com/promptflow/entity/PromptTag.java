package com.promptflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "prompt_tag")
public class PromptTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 20)
    private String color;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "is_system")
    private Boolean isSystem = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<PromptCache> prompts = new HashSet<>();

    // 默认构造函数
    public PromptTag() {}

    // 便捷构造函数
    public PromptTag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<PromptCache> getPrompts() {
        return prompts;
    }

    public void setPrompts(Set<PromptCache> prompts) {
        this.prompts = prompts;
    }

    // 便捷方法：增加使用次数
    public void incrementUsageCount() {
        this.usageCount = (this.usageCount == null ? 0 : this.usageCount) + 1;
    }

    @Override
    public String toString() {
        return "PromptTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", usageCount=" + usageCount +
                '}';
    }
}
