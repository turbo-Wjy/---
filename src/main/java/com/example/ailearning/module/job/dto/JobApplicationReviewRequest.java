package com.example.ailearning.module.job.dto;

import jakarta.validation.constraints.NotBlank;

public class JobApplicationReviewRequest {
    @NotBlank
    private String result;
    private String feedback;

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
