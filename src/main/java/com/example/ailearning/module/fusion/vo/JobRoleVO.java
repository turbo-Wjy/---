package com.example.ailearning.module.fusion.vo;

public class JobRoleVO {
    private Long id;
    private Long majorId;
    private String roleCode;
    private String roleName;
    private String description;
    private String typicalTasks;
    private String abilityTags;
    private Integer sortOrder;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTypicalTasks() { return typicalTasks; }
    public void setTypicalTasks(String typicalTasks) { this.typicalTasks = typicalTasks; }
    public String getAbilityTags() { return abilityTags; }
    public void setAbilityTags(String abilityTags) { this.abilityTags = abilityTags; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
