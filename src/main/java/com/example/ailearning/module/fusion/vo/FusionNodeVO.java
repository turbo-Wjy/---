package com.example.ailearning.module.fusion.vo;

import java.math.BigDecimal;

public class FusionNodeVO {
    private String nodeKey;
    private String nodeType;
    private Long nodeId;
    private String label;
    private String description;
    private BigDecimal score;
    private String masteryStatus;

    public String getNodeKey() { return nodeKey; }
    public void setNodeKey(String nodeKey) { this.nodeKey = nodeKey; }
    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getMasteryStatus() { return masteryStatus; }
    public void setMasteryStatus(String masteryStatus) { this.masteryStatus = masteryStatus; }
}
