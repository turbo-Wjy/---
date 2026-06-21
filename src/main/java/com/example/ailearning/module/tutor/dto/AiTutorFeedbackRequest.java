package com.example.ailearning.module.tutor.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AiTutorFeedbackRequest {
    @NotNull(message = "反馈评分不能为空")
    @DecimalMin(value = "0.00", message = "反馈评分不能小于0")
    @DecimalMax(value = "5.00", message = "反馈评分不能大于5")
    private BigDecimal feedbackScore;

    public BigDecimal getFeedbackScore() { return feedbackScore; }
    public void setFeedbackScore(BigDecimal feedbackScore) { this.feedbackScore = feedbackScore; }
}
