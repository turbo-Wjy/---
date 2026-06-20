package com.example.ailearning.module.statistics.vo;

import java.time.LocalDateTime;

public class ExportRecordVO {
    private Long id;
    private String exportType;
    private String exportScope;
    private Long majorId;
    private Long exportedBy;
    private String fileUrl;
    private Boolean desensitized;
    private String exportStatus;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExportType() { return exportType; }
    public void setExportType(String exportType) { this.exportType = exportType; }
    public String getExportScope() { return exportScope; }
    public void setExportScope(String exportScope) { this.exportScope = exportScope; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public Long getExportedBy() { return exportedBy; }
    public void setExportedBy(Long exportedBy) { this.exportedBy = exportedBy; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public Boolean getDesensitized() { return desensitized; }
    public void setDesensitized(Boolean desensitized) { this.desensitized = desensitized; }
    public String getExportStatus() { return exportStatus; }
    public void setExportStatus(String exportStatus) { this.exportStatus = exportStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
