package com.example.ailearning.module.course.vo;

import java.math.BigDecimal;

public class CourseVO {
    private Long id;
    private String courseCode;
    private String courseName;
    private Long majorId;
    private BigDecimal credit;
    private String semester;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
