package com.example.ailearning.module.evaluation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("learning_evaluations")
public class LearningEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String sourceType;
    private Long sourceId;
    private String evaluationSummary;
    @TableField("score_json")
    private String scoreJson;
    @TableField("suggestion_json")
    private String suggestionJson;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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
    public String getScoreJson() { return scoreJson; }
    public void setScoreJson(String scoreJson) { this.scoreJson = scoreJson; }
    public String getSuggestionJson() { return suggestionJson; }
    public void setSuggestionJson(String suggestionJson) { this.suggestionJson = suggestionJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
