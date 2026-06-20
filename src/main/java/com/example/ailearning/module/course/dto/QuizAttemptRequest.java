package com.example.ailearning.module.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class QuizAttemptRequest {
    @NotNull
    private Long studentId;
    @NotNull
    private Long courseId;
    private Long knowledgePointId;
    @NotBlank
    private String questionSnapshot;
    private String answer;
    private Boolean correct;
    private BigDecimal score;
    private String wrongReason;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public String getQuestionSnapshot() { return questionSnapshot; }
    public void setQuestionSnapshot(String questionSnapshot) { this.questionSnapshot = questionSnapshot; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getWrongReason() { return wrongReason; }
    public void setWrongReason(String wrongReason) { this.wrongReason = wrongReason; }
}
