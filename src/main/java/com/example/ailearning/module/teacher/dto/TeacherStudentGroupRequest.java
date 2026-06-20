package com.example.ailearning.module.teacher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TeacherStudentGroupRequest {
    @NotNull
    private Long teacherId;
    @NotNull
    private Long studentId;
    @NotBlank
    private String groupName;
    private String bindType = "custom_group";
    private String status = "active";

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getBindType() { return bindType; }
    public void setBindType(String bindType) { this.bindType = bindType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
