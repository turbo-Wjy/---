package com.example.ailearning.module.teacher.vo;

import java.time.LocalDateTime;
import java.util.List;

public class TeacherTaskVO {
    private Long id;
    private Long teacherId;
    private String taskTitle;
    private String taskType;
    private String taskDescription;
    private LocalDateTime dueAt;
    private String publishStatus;
    private String status;
    private List<TeacherTaskTargetVO> targets;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public LocalDateTime getDueAt() { return dueAt; }
    public void setDueAt(LocalDateTime dueAt) { this.dueAt = dueAt; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<TeacherTaskTargetVO> getTargets() { return targets; }
    public void setTargets(List<TeacherTaskTargetVO> targets) { this.targets = targets; }
}
