package com.example.ailearning.module.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class KnowledgePointRelationRequest {
    @NotNull
    private Long sourceKnowledgePointId;
    @NotNull
    private Long targetKnowledgePointId;
    @NotBlank
    private String relationType;
    private BigDecimal weight = BigDecimal.ONE;
    private String description;
    private String status = "active";

    public Long getSourceKnowledgePointId() { return sourceKnowledgePointId; }
    public void setSourceKnowledgePointId(Long sourceKnowledgePointId) { this.sourceKnowledgePointId = sourceKnowledgePointId; }
    public Long getTargetKnowledgePointId() { return targetKnowledgePointId; }
    public void setTargetKnowledgePointId(Long targetKnowledgePointId) { this.targetKnowledgePointId = targetKnowledgePointId; }
    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
