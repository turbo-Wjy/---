package com.example.ailearning.module.profile.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProfileSessionVO {
    private Long id;
    private Long studentId;
    private String sessionTitle;
    private String confirmStatus;
    private BigDecimal confidenceScore;
    private Long confirmedProfileId;
    private String draftProfile;
    private List<ProfileDimensionVO> dimensions;
    private List<ProfileMessageVO> messages;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
    public String getConfirmStatus() { return confirmStatus; }
    public void setConfirmStatus(String confirmStatus) { this.confirmStatus = confirmStatus; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }
    public Long getConfirmedProfileId() { return confirmedProfileId; }
    public void setConfirmedProfileId(Long confirmedProfileId) { this.confirmedProfileId = confirmedProfileId; }
    public String getDraftProfile() { return draftProfile; }
    public void setDraftProfile(String draftProfile) { this.draftProfile = draftProfile; }
    public List<ProfileDimensionVO> getDimensions() { return dimensions; }
    public void setDimensions(List<ProfileDimensionVO> dimensions) { this.dimensions = dimensions; }
    public List<ProfileMessageVO> getMessages() { return messages; }
    public void setMessages(List<ProfileMessageVO> messages) { this.messages = messages; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
