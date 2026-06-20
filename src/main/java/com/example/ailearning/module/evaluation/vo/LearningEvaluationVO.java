package com.example.ailearning.module.evaluation.vo;

import java.time.LocalDateTime;

public class LearningEvaluationVO {
    private Long id;
    private Long studentId;
    private String sourceType;
    private Long sourceId;
    private String evaluationSummary;
    private String score;
    private String suggestion;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getEvaluationSummary() { return evaluationSummary; }
    public void setEvaluationSummary(String evaluationSummary) { this.evaluationSummary = evaluationSummary; }
    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
