package com.example.ailearning.module.project.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectRequest {
    private Long courseId;
    @NotBlank
    private String title;
    private String description;
    private String difficultyLevel;
    private String abilityTags;

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public String getAbilityTags() { return abilityTags; }
    public void setAbilityTags(String abilityTags) { this.abilityTags = abilityTags; }
}
