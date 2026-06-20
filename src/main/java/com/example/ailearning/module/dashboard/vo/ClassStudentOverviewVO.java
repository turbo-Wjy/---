package com.example.ailearning.module.dashboard.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClassStudentOverviewVO {
    private Long studentId;
    private String studentNo;
    private String realName;
    private Long classId;
    private String grade;
    private String enrollmentStatus;
    private Integer profileVersion;
    private BigDecimal profileCompletenessScore;
    private LocalDateTime lastProfileGeneratedAt;
    private Integer learningPathProgressPercent;
    private Long weakPointCount;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public Integer getProfileVersion() { return profileVersion; }
    public void setProfileVersion(Integer profileVersion) { this.profileVersion = profileVersion; }
    public BigDecimal getProfileCompletenessScore() { return profileCompletenessScore; }
    public void setProfileCompletenessScore(BigDecimal profileCompletenessScore) { this.profileCompletenessScore = profileCompletenessScore; }
    public LocalDateTime getLastProfileGeneratedAt() { return lastProfileGeneratedAt; }
    public void setLastProfileGeneratedAt(LocalDateTime lastProfileGeneratedAt) { this.lastProfileGeneratedAt = lastProfileGeneratedAt; }
    public Integer getLearningPathProgressPercent() { return learningPathProgressPercent; }
    public void setLearningPathProgressPercent(Integer learningPathProgressPercent) { this.learningPathProgressPercent = learningPathProgressPercent; }
    public Long getWeakPointCount() { return weakPointCount; }
    public void setWeakPointCount(Long weakPointCount) { this.weakPointCount = weakPointCount; }
}
