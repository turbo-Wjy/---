package com.example.ailearning.module.student.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class StudentImportConfirmRequest {
    @Valid
    @NotEmpty
    private List<StudentImportRow> rows;

    public List<StudentImportRow> getRows() {
        return rows;
    }

    public void setRows(List<StudentImportRow> rows) {
        this.rows = rows;
    }
}
