package com.example.ailearning.module.employment.dto;

import jakarta.validation.constraints.NotBlank;

public class ResumeConfirmRequest {
    @NotBlank(message = "简历内容不能为空")
    private String resumeContent;
    private String resumeSummary;

    public String getResumeContent() { return resumeContent; }
    public void setResumeContent(String resumeContent) { this.resumeContent = resumeContent; }
    public String getResumeSummary() { return resumeSummary; }
    public void setResumeSummary(String resumeSummary) { this.resumeSummary = resumeSummary; }
}
