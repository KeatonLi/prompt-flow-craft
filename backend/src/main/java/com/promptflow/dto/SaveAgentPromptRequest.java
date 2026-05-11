package com.promptflow.dto;

/**
 * 保存Agent提示词的请求DTO
 */
public class SaveAgentPromptRequest {
    private String name;
    private String roleDescription;
    private String capabilities;
    private String behaviors;
    private String communicationStyle;
    private String generatedPrompt;

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

    public String getGeneratedPrompt() { return generatedPrompt; }
    public void setGeneratedPrompt(String generatedPrompt) { this.generatedPrompt = generatedPrompt; }
}
