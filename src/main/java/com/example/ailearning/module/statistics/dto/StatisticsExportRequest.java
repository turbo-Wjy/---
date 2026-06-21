package com.example.ailearning.module.statistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StatisticsExportRequest {
    @NotBlank(message = "导出类型不能为空")
    private String exportType;
    @NotNull(message = "专业ID不能为空")
    private Long majorId;
    private Boolean desensitized = true;

    public String getExportType() { return exportType; }
    public void setExportType(String exportType) { this.exportType = exportType; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public Boolean getDesensitized() { return desensitized; }
    public void setDesensitized(Boolean desensitized) { this.desensitized = desensitized; }
}
