package com.example.ailearning.module.dashboard.vo;

import java.math.BigDecimal;

public class ClassWeakPointVO {
    private String targetType;
    private Long targetId;
    private String label;
    private BigDecimal averageScore;
    private Long affectedStudentCount;
    private Long totalStudentCount;

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public Long getAffectedStudentCount() { return affectedStudentCount; }
    public void setAffectedStudentCount(Long affectedStudentCount) { this.affectedStudentCount = affectedStudentCount; }
    public Long getTotalStudentCount() { return totalStudentCount; }
    public void setTotalStudentCount(Long totalStudentCount) { this.totalStudentCount = totalStudentCount; }
}
