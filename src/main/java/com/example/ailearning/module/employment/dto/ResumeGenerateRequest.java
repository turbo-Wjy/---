package com.example.ailearning.module.employment.dto;

public class ResumeGenerateRequest {
    private Long targetJobId;
    private String targetPosition;

    public Long getTargetJobId() { return targetJobId; }
    public void setTargetJobId(Long targetJobId) { this.targetJobId = targetJobId; }
    public String getTargetPosition() { return targetPosition; }
    public void setTargetPosition(String targetPosition) { this.targetPosition = targetPosition; }
}
