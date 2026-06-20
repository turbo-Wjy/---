package com.example.ailearning.module.teacher.vo;

public class TeacherStudentGroupVO {
    private Long id;
    private Long teacherId;
    private Long studentId;
    private String groupName;
    private String bindType;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
