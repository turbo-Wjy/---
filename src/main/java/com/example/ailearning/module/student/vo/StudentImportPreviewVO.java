package com.example.ailearning.module.student.vo;

import com.example.ailearning.module.student.dto.StudentImportRow;

import java.util.List;

public class StudentImportPreviewVO {
    private int totalCount;
    private int validCount;
    private int invalidCount;
    private List<StudentImportRow> rows;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount) {
        this.invalidCount = invalidCount;
    }

    public List<StudentImportRow> getRows() {
        return rows;
    }

    public void setRows(List<StudentImportRow> rows) {
        this.rows = rows;
    }
}
