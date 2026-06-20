package com.example.ailearning.module.teacher.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class TeacherTaskRequest {
    @NotBlank
    private String taskTitle;
    @NotBlank
    private String taskType;
    private String taskDescription;
    private LocalDateTime dueAt;
    @NotBlank
    private String targetType;
    private List<Long> targetIds;

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public LocalDateTime getDueAt() { return dueAt; }
    public void setDueAt(LocalDateTime dueAt) { this.dueAt = dueAt; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public List<Long> getTargetIds() { return targetIds; }
    public void setTargetIds(List<Long> targetIds) { this.targetIds = targetIds; }
}
