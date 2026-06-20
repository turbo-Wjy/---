package com.example.ailearning.module.statistics.dto;

import jakarta.validation.constraints.NotBlank;

public class ExportRequest {
    @NotBlank
    private String exportType;
    @NotBlank
    private String exportScope;
    private Long majorId;
    private Boolean desensitized;

    public String getExportType() { return exportType; }
    public void setExportType(String exportType) { this.exportType = exportType; }
    public String getExportScope() { return exportScope; }
    public void setExportScope(String exportScope) { this.exportScope = exportScope; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public Boolean getDesensitized() { return desensitized; }
    public void setDesensitized(Boolean desensitized) { this.desensitized = desensitized; }
}
