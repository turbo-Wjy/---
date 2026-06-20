package com.example.ailearning.module.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("learning_paths")
public class LearningPath {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String title;
    private String goal;
    private Long generatedByAgentId;
    private String pathStatus;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
