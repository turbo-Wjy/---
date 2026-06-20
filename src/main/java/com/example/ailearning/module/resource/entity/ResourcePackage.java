package com.example.ailearning.module.resource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("resource_packages")
public class ResourcePackage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long taskId;
    private Long profileId;
    private Long targetJobRoleId;
    private Long courseId;
    private Long competitionId;
    private Long certificateId;
    private String packageTitle;
    @TableField("generation_context")
    private String generationContextJson;
    @TableField("resource_types")
    private String resourceTypesJson;
    private String difficulty;
    private String scenario;
    private String reviewStatus;
    private LocalDateTime publishedAt;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public Long getTargetJobRoleId() { return targetJobRoleId; }
    public void setTargetJobRoleId(Long targetJobRoleId) { this.targetJobRoleId = targetJobRoleId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }
    public String getPackageTitle() { return packageTitle; }
    public void setPackageTitle(String packageTitle) { this.packageTitle = packageTitle; }
    public String getGenerationContextJson() { return generationContextJson; }
    public void setGenerationContextJson(String generationContextJson) { this.generationContextJson = generationContextJson; }
    public String getResourceTypesJson() { return resourceTypesJson; }
    public void setResourceTypesJson(String resourceTypesJson) { this.resourceTypesJson = resourceTypesJson; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
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
