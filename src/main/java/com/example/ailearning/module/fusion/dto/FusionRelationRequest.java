package com.example.ailearning.module.fusion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FusionRelationRequest {
    @NotBlank
    private String sourceType;
    @NotNull
    private Long sourceId;
    @NotBlank
    private String targetType;
    @NotNull
    private Long targetId;
    @NotBlank
    private String relationType;
    private BigDecimal weight = BigDecimal.ONE;
    private String description;
    private Object evidence;
    private String status = "active";

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Object getEvidence() { return evidence; }
    public void setEvidence(Object evidence) { this.evidence = evidence; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
