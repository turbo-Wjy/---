package com.example.ailearning.module.achievement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ReviewRequest {
    @NotBlank(message = "审核结果不能为空")
    @Pattern(regexp = "approved|rejected", message = "审核结果只能是 approved 或 rejected")
    private String reviewResult;
    private String reviewComment;

    public String getReviewResult() { return reviewResult; }
    public void setReviewResult(String reviewResult) { this.reviewResult = reviewResult; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
}
