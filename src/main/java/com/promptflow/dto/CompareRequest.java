package com.promptflow.dto;

import java.util.List;

public class CompareRequest {
    private String promptA;
    private String promptB;
    
    public String getPromptA() {
        return promptA;
    }
    
    public void setPromptA(String promptA) {
        this.promptA = promptA;
    }
    
    public String getPromptB() {
        return promptB;
    }
    
    public void setPromptB(String promptB) {
        this.promptB = promptB;
    }
}
