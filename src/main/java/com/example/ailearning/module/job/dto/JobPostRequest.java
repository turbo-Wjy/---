package com.example.ailearning.module.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JobPostRequest {
    @NotNull
    private Long enterpriseId;
    @NotNull
    private Long mentorId;
    @NotNull
    private Long majorId;
    @NotBlank
    private String title;
    private String requirements;
    private String salaryRange;
    private String location;
    private String abilityTags;

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
    public String getAbilityTags() { return abilityTags; }
    public void setAbilityTags(String abilityTags) { this.abilityTags = abilityTags; }
}
