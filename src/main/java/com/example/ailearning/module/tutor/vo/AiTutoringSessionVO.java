package com.example.ailearning.module.tutor.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class AiTutoringSessionVO {
    private Long id;
    private Long studentId;
    private Long knowledgePointId;
    private String knowledgePointName;
    private String question;
    private String answerText;
    private Map<String, Object> answerAssets;
    private BigDecimal feedbackScore;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public String getKnowledgePointName() { return knowledgePointName; }
    public void setKnowledgePointName(String knowledgePointName) { this.knowledgePointName = knowledgePointName; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public Map<String, Object> getAnswerAssets() { return answerAssets; }
    public void setAnswerAssets(Map<String, Object> answerAssets) { this.answerAssets = answerAssets; }
    public BigDecimal getFeedbackScore() { return feedbackScore; }
    public void setFeedbackScore(BigDecimal feedbackScore) { this.feedbackScore = feedbackScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
