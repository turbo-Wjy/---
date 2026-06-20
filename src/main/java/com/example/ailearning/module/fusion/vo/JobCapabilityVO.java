package com.example.ailearning.module.fusion.vo;

import java.math.BigDecimal;

public class JobCapabilityVO {
    private Long id;
    private Long jobRoleId;
    private Long parentId;
    private String capabilityCode;
    private String capabilityName;
    private String description;
    private String level;
    private BigDecimal weight;
    private Integer sortOrder;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJobRoleId() { return jobRoleId; }
    public void setJobRoleId(Long jobRoleId) { this.jobRoleId = jobRoleId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getCapabilityCode() { return capabilityCode; }
    public void setCapabilityCode(String capabilityCode) { this.capabilityCode = capabilityCode; }
    public String getCapabilityName() { return capabilityName; }
    public void setCapabilityName(String capabilityName) { this.capabilityName = capabilityName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
