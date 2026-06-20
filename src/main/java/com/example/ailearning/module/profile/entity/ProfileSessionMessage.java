package com.example.ailearning.module.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("profile_session_messages")
public class ProfileSessionMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sessionId;
    private String messageRole;
    private String messageContentEncrypted;
    private String messageContentIv;
    private String extractedFeaturesEncrypted;
    private String extractedFeaturesIv;
    @TableField("token_usage")
    private String tokenUsageJson;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getMessageRole() { return messageRole; }
    public void setMessageRole(String messageRole) { this.messageRole = messageRole; }
    public String getMessageContentEncrypted() { return messageContentEncrypted; }
    public void setMessageContentEncrypted(String messageContentEncrypted) { this.messageContentEncrypted = messageContentEncrypted; }
    public String getMessageContentIv() { return messageContentIv; }
    public void setMessageContentIv(String messageContentIv) { this.messageContentIv = messageContentIv; }
    public String getExtractedFeaturesEncrypted() { return extractedFeaturesEncrypted; }
    public void setExtractedFeaturesEncrypted(String extractedFeaturesEncrypted) { this.extractedFeaturesEncrypted = extractedFeaturesEncrypted; }
    public String getExtractedFeaturesIv() { return extractedFeaturesIv; }
    public void setExtractedFeaturesIv(String extractedFeaturesIv) { this.extractedFeaturesIv = extractedFeaturesIv; }
    public String getTokenUsageJson() { return tokenUsageJson; }
    public void setTokenUsageJson(String tokenUsageJson) { this.tokenUsageJson = tokenUsageJson; }
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
