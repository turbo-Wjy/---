package com.example.ailearning.module.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("knowledge_point_relations")
public class KnowledgePointRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sourceKnowledgePointId;
    private Long targetKnowledgePointId;
    private String relationType;
    private BigDecimal weight;
    private String description;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
