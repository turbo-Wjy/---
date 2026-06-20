package com.example.ailearning.module.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("resource_recommendations")
public class ResourceRecommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long resourceId;
    private String recommendReason;
    private Long sourceProfileId;
    private String viewStatus;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
