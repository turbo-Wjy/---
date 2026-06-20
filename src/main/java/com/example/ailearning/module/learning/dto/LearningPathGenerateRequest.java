package com.example.ailearning.module.learning.dto;

import java.util.List;

public class LearningPathGenerateRequest {
    private Long targetJobRoleId;
    private Long competitionId;
    private Long certificateId;
    private String learningGoal;
    private Integer durationWeeks = 4;
    private List<String> preferredResourceTypes;

    public Long getTargetJobRoleId() { return targetJobRoleId; }
    public void setTargetJobRoleId(Long targetJobRoleId) { this.targetJobRoleId = targetJobRoleId; }
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }
    public String getLearningGoal() { return learningGoal; }
    public void setLearningGoal(String learningGoal) { this.learningGoal = learningGoal; }
    public Integer getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }
    public List<String> getPreferredResourceTypes() { return preferredResourceTypes; }
    public void setPreferredResourceTypes(List<String> preferredResourceTypes) { this.preferredResourceTypes = preferredResourceTypes; }
}
