package com.example.ailearning.module.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("profile_update_logs")
public class ProfileUpdateLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String sourceType;
    private Long sourceId;
    @TableField("before_snapshot")
    private String beforeSnapshotJson;
    @TableField("after_snapshot")
    private String afterSnapshotJson;
    private String updatedReason;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getBeforeSnapshotJson() { return beforeSnapshotJson; }
    public void setBeforeSnapshotJson(String beforeSnapshotJson) { this.beforeSnapshotJson = beforeSnapshotJson; }
    public String getAfterSnapshotJson() { return afterSnapshotJson; }
    public void setAfterSnapshotJson(String afterSnapshotJson) { this.afterSnapshotJson = afterSnapshotJson; }
    public String getUpdatedReason() { return updatedReason; }
    public void setUpdatedReason(String updatedReason) { this.updatedReason = updatedReason; }
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
