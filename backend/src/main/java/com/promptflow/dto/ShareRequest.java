package com.promptflow.dto;

public class ShareRequest {
    private String description;
    private String promptContent;
    private String authorNickname;
    private String authorContact;
    private Long sourcePromptId;

    public ShareRequest() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPromptContent() {
        return promptContent;
    }

    public void setPromptContent(String promptContent) {
        this.promptContent = promptContent;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getAuthorContact() {
        return authorContact;
    }

    public void setAuthorContact(String authorContact) {
        this.authorContact = authorContact;
    }

    public Long getSourcePromptId() {
        return sourcePromptId;
    }

    public void setSourcePromptId(Long sourcePromptId) {
        this.sourcePromptId = sourcePromptId;
    }
}
