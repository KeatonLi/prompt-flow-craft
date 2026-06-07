package com.promptflow.strategy.pipeline.dto;

/**
 * Pipeline 统一请求 DTO
 * 同时承载 Agent 和 Skill 两种表单的输入数据
 */
public class PipelineRequest {

    // ========== 通用字段 ==========
    /** prompt 类型：agent / skill */
    private String promptType;

    // ========== Agent 字段 ==========
    private String name;
    private String roleDescription;
    private String capabilities;
    private String behaviors;
    private String communicationStyle;

    // ========== Skill 字段 ==========
    private String description;
    private String skillType;
    private String method;
    private String endpoint;
    private String parameters;
    private String outputDescription;

    // ========== 通用 Getters/Setters ==========
    public String getPromptType() { return promptType; }
    public void setPromptType(String promptType) { this.promptType = promptType; }

    // ========== Agent Getters/Setters ==========
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

    // ========== Skill Getters/Setters ==========
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
}
