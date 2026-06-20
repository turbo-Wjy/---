package com.example.ailearning.module.teacher.dto;

import com.example.ailearning.common.pagination.PageQuery;

public class TeacherPageQuery extends PageQuery {
    private Long collegeId;

    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
}
