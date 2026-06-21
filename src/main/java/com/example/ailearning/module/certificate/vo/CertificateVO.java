package com.example.ailearning.module.certificate.vo;

import java.time.LocalDateTime;

public class CertificateVO {
    private Long id;
    private Long majorId;
    private String certificateName;
    private String requirementLevel;
    private Boolean graduationRequired;
    private String resourceUrl;
    private Long importedBy;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Long getImportedBy() { return importedBy; }
    public void setImportedBy(Long importedBy) { this.importedBy = importedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
