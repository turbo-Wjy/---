package com.example.ailearning.module.job.dto;

public class ResumeGenerateRequest {
    private Long targetJobId;
    private String resumeContent;
    private String resumeSummary;
    private Boolean confirm;

    public Long getTargetJobId() { return targetJobId; }
    public void setTargetJobId(Long targetJobId) { this.targetJobId = targetJobId; }
    public String getResumeContent() { return resumeContent; }
    public void setResumeContent(String resumeContent) { this.resumeContent = resumeContent; }
    public String getResumeSummary() { return resumeSummary; }
    public void setResumeSummary(String resumeSummary) { this.resumeSummary = resumeSummary; }
    public Boolean getConfirm() { return confirm; }
    public void setConfirm(Boolean confirm) { this.confirm = confirm; }
}
