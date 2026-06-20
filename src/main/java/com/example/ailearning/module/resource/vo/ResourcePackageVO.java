package com.example.ailearning.module.resource.vo;

import java.time.LocalDateTime;
import java.util.List;

public class ResourcePackageVO {
    private Long id;
    private Long studentId;
    private Long taskId;
    private Long profileId;
    private Long targetJobRoleId;
    private Long courseId;
    private Long competitionId;
    private Long certificateId;
    private String packageTitle;
    private String generationContext;
    private String resourceTypes;
    private String difficulty;
    private String scenario;
    private String reviewStatus;
    private String status;
    private LocalDateTime publishedAt;
    private List<AiGeneratedResourceVO> resources;

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
    public String getGenerationContext() { return generationContext; }
    public void setGenerationContext(String generationContext) { this.generationContext = generationContext; }
    public String getResourceTypes() { return resourceTypes; }
    public void setResourceTypes(String resourceTypes) { this.resourceTypes = resourceTypes; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public List<AiGeneratedResourceVO> getResources() { return resources; }
    public void setResources(List<AiGeneratedResourceVO> resources) { this.resources = resources; }
}
