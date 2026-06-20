package com.example.ailearning.module.certificate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CertificateRequest {
    @NotNull
    private Long majorId;
    @NotBlank
    private String certificateName;
    private String requirementLevel;
    private Boolean graduationRequired;
    private String resourceUrl;

    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public String getCertificateName() { return certificateName; }
    public void setCertificateName(String certificateName) { this.certificateName = certificateName; }
    public String getRequirementLevel() { return requirementLevel; }
    public void setRequirementLevel(String requirementLevel) { this.requirementLevel = requirementLevel; }
    public Boolean getGraduationRequired() { return graduationRequired; }
    public void setGraduationRequired(Boolean graduationRequired) { this.graduationRequired = graduationRequired; }
    public String getResourceUrl() { return resourceUrl; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }
}
