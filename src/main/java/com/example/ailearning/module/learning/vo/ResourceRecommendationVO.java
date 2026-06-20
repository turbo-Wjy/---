package com.example.ailearning.module.learning.vo;

import com.example.ailearning.module.resource.vo.AiGeneratedResourceVO;

import java.time.LocalDateTime;

public class ResourceRecommendationVO {
    private Long id;
    private Long studentId;
    private Long resourceId;
    private String recommendReason;
    private Long sourceProfileId;
    private String viewStatus;
    private String status;
    private LocalDateTime createdAt;
    private AiGeneratedResourceVO resource;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public String getRecommendReason() { return recommendReason; }
    public void setRecommendReason(String recommendReason) { this.recommendReason = recommendReason; }
    public Long getSourceProfileId() { return sourceProfileId; }
    public void setSourceProfileId(Long sourceProfileId) { this.sourceProfileId = sourceProfileId; }
    public String getViewStatus() { return viewStatus; }
    public void setViewStatus(String viewStatus) { this.viewStatus = viewStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public AiGeneratedResourceVO getResource() { return resource; }
    public void setResource(AiGeneratedResourceVO resource) { this.resource = resource; }
}
