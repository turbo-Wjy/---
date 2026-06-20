package com.example.ailearning.module.role.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    @NotBlank
    private String dataScope;
    private Boolean core = false;
    private String description;
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

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public Boolean getCore() {
        return core;
    }

    public void setCore(Boolean core) {
        this.core = core;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
