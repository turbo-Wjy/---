package com.example.ailearning.module.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("resumes")
public class Resume {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long targetJobId;
    private Long generatedByTaskId;
    private String resumeContentEncrypted;
    private String resumeContentIv;
    private String resumeSummary;
    private Boolean studentConfirmed;
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
    public Long getTargetJobId() { return targetJobId; }
    public void setTargetJobId(Long targetJobId) { this.targetJobId = targetJobId; }
    public Long getGeneratedByTaskId() { return generatedByTaskId; }
    public void setGeneratedByTaskId(Long generatedByTaskId) { this.generatedByTaskId = generatedByTaskId; }
    public String getResumeContentEncrypted() { return resumeContentEncrypted; }
    public void setResumeContentEncrypted(String resumeContentEncrypted) { this.resumeContentEncrypted = resumeContentEncrypted; }
    public String getResumeContentIv() { return resumeContentIv; }
    public void setResumeContentIv(String resumeContentIv) { this.resumeContentIv = resumeContentIv; }
    public String getResumeSummary() { return resumeSummary; }
    public void setResumeSummary(String resumeSummary) { this.resumeSummary = resumeSummary; }
    public Boolean getStudentConfirmed() { return studentConfirmed; }
    public void setStudentConfirmed(Boolean studentConfirmed) { this.studentConfirmed = studentConfirmed; }
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
