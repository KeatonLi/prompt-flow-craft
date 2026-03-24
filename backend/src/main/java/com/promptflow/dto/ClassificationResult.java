package com.promptflow.dto;

import java.util.List;

/**
 * 分类结果数据传输对象
 */
public class ClassificationResult {

    private Long categoryId;
    private String categoryName;
    private List<String> tags;
    private double confidence;

    public ClassificationResult() {}

    public ClassificationResult(Long categoryId, String categoryName, List<String> tags, double confidence) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.tags = tags;
        this.confidence = confidence;
    }

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "ClassificationResult{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", tags=" + tags +
                ", confidence=" + confidence +
                '}';
    }
}
