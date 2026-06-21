package com.example.ailearning.module.employment.vo;

import java.time.LocalDateTime;

public class ResumeVO {
    private Long id;
    private Long studentId;
    private Long targetJobId;
    private Long generatedByTaskId;
    private String resumeContent;
    private String resumeSummary;
    private Boolean studentConfirmed;
    private LocalDateTime confirmedAt;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getTargetJobId() { return targetJobId; }
    public void setTargetJobId(Long targetJobId) { this.targetJobId = targetJobId; }
    public Long getGeneratedByTaskId() { return generatedByTaskId; }
    public void setGeneratedByTaskId(Long generatedByTaskId) { this.generatedByTaskId = generatedByTaskId; }
    public String getResumeContent() { return resumeContent; }
    public void setResumeContent(String resumeContent) { this.resumeContent = resumeContent; }
    public String getResumeSummary() { return resumeSummary; }
    public void setResumeSummary(String resumeSummary) { this.resumeSummary = resumeSummary; }
    public Boolean getStudentConfirmed() { return studentConfirmed; }
    public void setStudentConfirmed(Boolean studentConfirmed) { this.studentConfirmed = studentConfirmed; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
