package com.promptflow.dto;

import java.time.LocalDateTime;

public interface PromptListItem {
    Long getId();
    String getTaskDescription();
    String getTargetAudience();
    String getOutputFormat();
    String getTone();
    String getLength();
    String getPromptSummary();
    LocalDateTime getCreatedAt();
    Integer getHitCount();
    Long getCategoryId();
    Integer getLikeCount();
    Boolean getIsAutoTagged();
}
