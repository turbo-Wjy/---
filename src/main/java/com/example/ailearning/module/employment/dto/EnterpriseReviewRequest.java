package com.example.ailearning.module.employment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EnterpriseReviewRequest {
    @NotBlank(message = "审核结果不能为空")
    @Pattern(regexp = "approved|rejected|recommended|interview|pending_communication", message = "企业审核结果不合法")
    private String reviewResult;
    private String feedback;

    public String getReviewResult() { return reviewResult; }
    public void setReviewResult(String reviewResult) { this.reviewResult = reviewResult; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
