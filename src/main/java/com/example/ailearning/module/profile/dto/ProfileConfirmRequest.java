package com.example.ailearning.module.profile.dto;

import jakarta.validation.constraints.NotNull;

public class ProfileConfirmRequest {
    @NotNull
    private Long sessionId;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
}
