package com.example.ailearning.module.employment.vo;

import java.time.LocalDateTime;

public class JobApplicationVO {
    private Long id;
    private Long jobId;
    private Long resumeId;
    private Long studentId;
    private String applicationStatus;
    private LocalDateTime submittedAt;
    private String enterpriseFeedback;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getEnterpriseFeedback() { return enterpriseFeedback; }
    public void setEnterpriseFeedback(String enterpriseFeedback) { this.enterpriseFeedback = enterpriseFeedback; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
