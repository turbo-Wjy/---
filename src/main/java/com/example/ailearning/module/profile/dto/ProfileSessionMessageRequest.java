package com.example.ailearning.module.profile.dto;

import jakarta.validation.constraints.NotBlank;

public class ProfileSessionMessageRequest {
    @NotBlank
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
