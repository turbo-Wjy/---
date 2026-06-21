package com.example.ailearning.module.certificate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CertificateRequest {
    @NotNull(message = "专业ID不能为空")
    private Long majorId;
    @NotBlank(message = "证书名称不能为空")
    private String certificateName;
    private String requirementLevel;
    private Boolean graduationRequired;
    private String resourceUrl;
    private String status;

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
