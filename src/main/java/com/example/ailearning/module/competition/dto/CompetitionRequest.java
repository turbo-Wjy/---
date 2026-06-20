package com.example.ailearning.module.competition.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CompetitionRequest {
    @NotBlank
    private String title;
    private String level;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String requirements;
    private String officialUrl;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getOfficialUrl() { return officialUrl; }
    public void setOfficialUrl(String officialUrl) { this.officialUrl = officialUrl; }
}
