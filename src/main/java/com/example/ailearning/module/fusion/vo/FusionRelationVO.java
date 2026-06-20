package com.example.ailearning.module.fusion.vo;

import java.math.BigDecimal;

public class FusionRelationVO {
    private Long id;
    private String sourceType;
    private Long sourceId;
    private String targetType;
    private Long targetId;
    private String relationType;
    private BigDecimal weight;
    private String description;
    private String evidence;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
