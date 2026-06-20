package com.example.ailearning.module.teacher.vo;

import java.util.List;

public class TeacherVO {
    private Long id;
    private Long userId;
    private String teacherNo;
    private String realName;
    private Long collegeId;
    private String title;
    private String status;
    private List<String> dutyTags;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
    public List<String> getDutyTags() { return dutyTags; }
    public void setDutyTags(List<String> dutyTags) { this.dutyTags = dutyTags; }
}
