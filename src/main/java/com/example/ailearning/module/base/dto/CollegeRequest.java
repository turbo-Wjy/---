package com.example.ailearning.module.base.dto;

import jakarta.validation.constraints.NotBlank;

public class CollegeRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String status = "active";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
