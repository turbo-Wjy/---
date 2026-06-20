package com.example.ailearning.module.learning.vo;

import java.time.LocalDateTime;
import java.util.List;

public class LearningPathVO {
    private Long id;
    private Long studentId;
    private String title;
    private String goal;
    private Long generatedByAgentId;
    private String pathStatus;
    private String status;
    private LocalDateTime createdAt;
    private List<LearningPathStepVO> steps;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public Long getGeneratedByAgentId() { return generatedByAgentId; }
    public void setGeneratedByAgentId(Long generatedByAgentId) { this.generatedByAgentId = generatedByAgentId; }
    public String getPathStatus() { return pathStatus; }
    public void setPathStatus(String pathStatus) { this.pathStatus = pathStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<LearningPathStepVO> getSteps() { return steps; }
    public void setSteps(List<LearningPathStepVO> steps) { this.steps = steps; }
}
