package com.example.ailearning.module.resource.vo;

public class AiGeneratedResourceVO {
    private Long id;
    private Long taskId;
    private String resourceType;
    private String title;
    private String contentUrl;
    private String contentText;
    private String metadata;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
