package com.example.ailearning.module.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("profile_sessions")
public class ProfileSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long agentId;
    private String sessionTitle;
    @TableField("draft_profile_json")
    private String draftProfileJson;
    @TableField("extracted_dimensions_json")
    private String extractedDimensionsJson;
    private BigDecimal confidenceScore;
    private String confirmStatus;
    private Long confirmedProfileId;
    private LocalDateTime confirmedAt;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
    public String getDraftProfileJson() { return draftProfileJson; }
    public void setDraftProfileJson(String draftProfileJson) { this.draftProfileJson = draftProfileJson; }
    public String getExtractedDimensionsJson() { return extractedDimensionsJson; }
    public void setExtractedDimensionsJson(String extractedDimensionsJson) { this.extractedDimensionsJson = extractedDimensionsJson; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getConfirmStatus() { return confirmStatus; }
    public void setConfirmStatus(String confirmStatus) { this.confirmStatus = confirmStatus; }
    public Long getConfirmedProfileId() { return confirmedProfileId; }
    public void setConfirmedProfileId(Long confirmedProfileId) { this.confirmedProfileId = confirmedProfileId; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
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
