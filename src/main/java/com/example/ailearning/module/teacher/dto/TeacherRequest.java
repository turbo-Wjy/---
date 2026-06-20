package com.example.ailearning.module.teacher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TeacherRequest {
    @NotBlank
    private String teacherNo;
    @NotBlank
    private String realName;
    @NotNull
    private Long collegeId;
    private String title;
    private String status = "active";

    public String getTeacherNo() { return teacherNo; }
    public void setTeacherNo(String teacherNo) { this.teacherNo = teacherNo; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
