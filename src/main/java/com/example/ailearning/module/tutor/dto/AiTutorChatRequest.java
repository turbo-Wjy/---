package com.example.ailearning.module.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiTutorChatRequest {
    private Long knowledgePointId;
    @NotBlank(message = "问题不能为空")
    @Size(max = 2000, message = "问题不能超过2000字")
    private String question;
    private String answerMode;

    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswerMode() { return answerMode; }
    public void setAnswerMode(String answerMode) { this.answerMode = answerMode; }
}
