package com.example.ailearning.module.course.vo;

public class WrongQuestionVO {
    private Long id;
    private Long studentId;
    private Long quizAttemptId;
    private Long knowledgePointId;
    private String wrongReason;
    private String reviewStatus;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getQuizAttemptId() { return quizAttemptId; }
    public void setQuizAttemptId(Long quizAttemptId) { this.quizAttemptId = quizAttemptId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public String getWrongReason() { return wrongReason; }
    public void setWrongReason(String wrongReason) { this.wrongReason = wrongReason; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
