package com.example.ailearning.module.course.vo;

import java.math.BigDecimal;

public class KnowledgePointRelationVO {
    private Long id;
    private Long sourceKnowledgePointId;
    private Long targetKnowledgePointId;
    private String relationType;
    private BigDecimal weight;
    private String description;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
