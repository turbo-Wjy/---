package com.example.ailearning.module.certificate.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CertificateResultRequest {
    @NotNull
    private Long certificateId;
    @NotNull
    private Long studentId;
    private String certificateNo;
    private LocalDate issuedAt;
    private String proofFileUrl;

    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getCertificateNo() { return certificateNo; }
    public void setCertificateNo(String certificateNo) { this.certificateNo = certificateNo; }
    public LocalDate getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDate issuedAt) { this.issuedAt = issuedAt; }
    public String getProofFileUrl() { return proofFileUrl; }
    public void setProofFileUrl(String proofFileUrl) { this.proofFileUrl = proofFileUrl; }
}
