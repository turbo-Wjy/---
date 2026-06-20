package com.example.ailearning.module.resource.vo;

import java.time.LocalDateTime;

public class AiGenerationTaskLogVO {
    private Long id;
    private Long taskId;
    private Long agentId;
    private String logLevel;
    private String logMessage;
    private String payload;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    public String getLogMessage() { return logMessage; }
    public void setLogMessage(String logMessage) { this.logMessage = logMessage; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
