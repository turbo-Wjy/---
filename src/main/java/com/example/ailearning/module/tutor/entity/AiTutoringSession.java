package com.example.ailearning.module.tutor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("ai_tutoring_sessions")
public class AiTutoringSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long knowledgePointId;
    private String questionEncrypted;
    private String questionIv;
    private String answerTextEncrypted;
    private String answerTextIv;
    @TableField("answer_assets_json")
    private String answerAssetsJson;
    private BigDecimal feedbackScore;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public String getQuestionEncrypted() { return questionEncrypted; }
    public void setQuestionEncrypted(String questionEncrypted) { this.questionEncrypted = questionEncrypted; }
    public String getQuestionIv() { return questionIv; }
    public void setQuestionIv(String questionIv) { this.questionIv = questionIv; }
    public String getAnswerTextEncrypted() { return answerTextEncrypted; }
    public void setAnswerTextEncrypted(String answerTextEncrypted) { this.answerTextEncrypted = answerTextEncrypted; }
    public String getAnswerTextIv() { return answerTextIv; }
    public void setAnswerTextIv(String answerTextIv) { this.answerTextIv = answerTextIv; }
    public String getAnswerAssetsJson() { return answerAssetsJson; }
    public void setAnswerAssetsJson(String answerAssetsJson) { this.answerAssetsJson = answerAssetsJson; }
    public BigDecimal getFeedbackScore() { return feedbackScore; }
    public void setFeedbackScore(BigDecimal feedbackScore) { this.feedbackScore = feedbackScore; }
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
