package com.example.ailearning.module.employment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class JobPostRequest {
    @NotNull(message = "专业ID不能为空")
    private Long majorId;
    @NotBlank(message = "岗位名称不能为空")
    private String title;
    private String requirements;
    private String salaryRange;
    private String location;
    private List<String> abilityTags;
    private Boolean submitReview = true;

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
    public Boolean getSubmitReview() { return submitReview; }
    public void setSubmitReview(Boolean submitReview) { this.submitReview = submitReview; }
}
