package com.example.ailearning.module.evaluation.dto;

public class LearningEvaluationGenerateRequest {
    private String sourceType = "overall";
    private Long sourceId;

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
}
