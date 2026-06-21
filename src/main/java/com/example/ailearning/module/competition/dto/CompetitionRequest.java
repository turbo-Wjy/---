package com.example.ailearning.module.competition.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CompetitionRequest {
    @NotBlank(message = "竞赛名称不能为空")
    private String title;
    private String level;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String requirements;
    private String officialUrl;
    private String status;

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
