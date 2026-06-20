package com.example.ailearning.module.student.dto;

import com.example.ailearning.common.pagination.PageQuery;

public class StudentPageQuery extends PageQuery {
    private Long majorId;
    private Long classId;
    private String grade;

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
