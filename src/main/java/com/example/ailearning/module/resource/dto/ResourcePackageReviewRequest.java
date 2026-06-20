package com.example.ailearning.module.resource.dto;

import jakarta.validation.constraints.NotBlank;

public class ResourcePackageReviewRequest {
    @NotBlank
    private String result;
    private String comment;

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
