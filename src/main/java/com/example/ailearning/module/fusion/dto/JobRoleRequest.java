package com.example.ailearning.module.fusion.dto;

import jakarta.validation.constraints.NotBlank;

public class JobRoleRequest {
    private Long majorId;
    @NotBlank
    private String roleCode;
    @NotBlank
    private String roleName;
    private String description;
    private Object typicalTasks;
    private Object abilityTags;
    private Integer sortOrder = 0;
    private String status = "active";

    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Object getTypicalTasks() { return typicalTasks; }
    public void setTypicalTasks(Object typicalTasks) { this.typicalTasks = typicalTasks; }
    public Object getAbilityTags() { return abilityTags; }
    public void setAbilityTags(Object abilityTags) { this.abilityTags = abilityTags; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
