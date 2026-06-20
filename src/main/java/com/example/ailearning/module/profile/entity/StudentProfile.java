package com.example.ailearning.module.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("student_profiles")
public class StudentProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Integer profileVersion;
    private String profileSummaryEncrypted;
    private String profileSummaryIv;
    private BigDecimal completenessScore;
    private LocalDateTime lastGeneratedAt;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Integer getProfileVersion() { return profileVersion; }
    public void setProfileVersion(Integer profileVersion) { this.profileVersion = profileVersion; }
    public String getProfileSummaryEncrypted() { return profileSummaryEncrypted; }
    public void setProfileSummaryEncrypted(String profileSummaryEncrypted) { this.profileSummaryEncrypted = profileSummaryEncrypted; }
    public String getProfileSummaryIv() { return profileSummaryIv; }
    public void setProfileSummaryIv(String profileSummaryIv) { this.profileSummaryIv = profileSummaryIv; }
    public BigDecimal getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(BigDecimal completenessScore) { this.completenessScore = completenessScore; }
    public LocalDateTime getLastGeneratedAt() { return lastGeneratedAt; }
    public void setLastGeneratedAt(LocalDateTime lastGeneratedAt) { this.lastGeneratedAt = lastGeneratedAt; }
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
