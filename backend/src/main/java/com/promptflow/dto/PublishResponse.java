package com.promptflow.dto;

public class PublishResponse {
    private Long id;
    private String deleteToken;

    public PublishResponse() {}

    public PublishResponse(Long id, String deleteToken) {
        this.id = id;
        this.deleteToken = deleteToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeleteToken() {
        return deleteToken;
    }

    public void setDeleteToken(String deleteToken) {
        this.deleteToken = deleteToken;
    }
}
