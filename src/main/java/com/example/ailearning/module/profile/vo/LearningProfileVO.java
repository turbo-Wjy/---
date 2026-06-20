package com.example.ailearning.module.profile.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LearningProfileVO {
    private Long id;
    private Long studentId;
    private Integer profileVersion;
    private String profileSummary;
    private BigDecimal completenessScore;
    private LocalDateTime lastGeneratedAt;
    private List<ProfileDimensionVO> dimensions;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Integer getProfileVersion() { return profileVersion; }
    public void setProfileVersion(Integer profileVersion) { this.profileVersion = profileVersion; }
    public String getProfileSummary() { return profileSummary; }
    public void setProfileSummary(String profileSummary) { this.profileSummary = profileSummary; }
    public BigDecimal getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(BigDecimal completenessScore) { this.completenessScore = completenessScore; }
    public LocalDateTime getLastGeneratedAt() { return lastGeneratedAt; }
    public void setLastGeneratedAt(LocalDateTime lastGeneratedAt) { this.lastGeneratedAt = lastGeneratedAt; }
    public List<ProfileDimensionVO> getDimensions() { return dimensions; }
    public void setDimensions(List<ProfileDimensionVO> dimensions) { this.dimensions = dimensions; }
}
