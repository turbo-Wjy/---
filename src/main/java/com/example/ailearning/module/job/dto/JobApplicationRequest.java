package com.example.ailearning.module.job.dto;

import jakarta.validation.constraints.NotNull;

public class JobApplicationRequest {
    @NotNull
    private Long jobId;
    @NotNull
    private Long resumeId;

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
}
