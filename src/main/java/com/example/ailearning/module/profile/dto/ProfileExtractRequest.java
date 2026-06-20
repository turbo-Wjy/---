package com.example.ailearning.module.profile.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProfileExtractRequest {
    private String profileSummary;
    private BigDecimal confidenceScore;
    private List<ProfileDimensionDraft> dimensions;

    public String getProfileSummary() { return profileSummary; }
    public void setProfileSummary(String profileSummary) { this.profileSummary = profileSummary; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }
    public List<ProfileDimensionDraft> getDimensions() { return dimensions; }
    public void setDimensions(List<ProfileDimensionDraft> dimensions) { this.dimensions = dimensions; }
}
