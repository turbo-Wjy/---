package com.example.ailearning.module.employment.vo;

import java.time.LocalDateTime;
import java.util.List;

public class JobPostVO {
    private Long id;
    private Long enterpriseId;
    private Long mentorId;
    private Long majorId;
    private String title;
    private String requirements;
    private String salaryRange;
    private String location;
    private List<String> abilityTags;
    private String reviewStatus;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Long enterpriseId) { this.enterpriseId = enterpriseId; }
    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getAbilityTags() { return abilityTags; }
    public void setAbilityTags(List<String> abilityTags) { this.abilityTags = abilityTags; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
