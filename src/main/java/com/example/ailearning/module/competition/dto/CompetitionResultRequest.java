package com.example.ailearning.module.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CompetitionResultRequest {
    @NotNull
    private Long competitionId;
    @NotNull
    private Long studentId;
    private Long coachTeacherId;
    @NotBlank
    private String awardName;
    private String proofFileUrl;

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCoachTeacherId() { return coachTeacherId; }
    public void setCoachTeacherId(Long coachTeacherId) { this.coachTeacherId = coachTeacherId; }
    public String getAwardName() { return awardName; }
    public void setAwardName(String awardName) { this.awardName = awardName; }
    public String getProofFileUrl() { return proofFileUrl; }
    public void setProofFileUrl(String proofFileUrl) { this.proofFileUrl = proofFileUrl; }
}
