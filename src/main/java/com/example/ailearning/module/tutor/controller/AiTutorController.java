package com.example.ailearning.module.tutor.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.tutor.dto.AiTutorChatRequest;
import com.example.ailearning.module.tutor.dto.AiTutorFeedbackRequest;
import com.example.ailearning.module.tutor.service.AiTutorService;
import com.example.ailearning.module.tutor.vo.AiTutoringSessionVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai-tutor")
public class AiTutorController {
    private final AiTutorService aiTutorService;

    public AiTutorController(AiTutorService aiTutorService) {
        this.aiTutorService = aiTutorService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyAuthority('ai_tutor.chat_self','ai_tutor.chat.self')")
    public ApiResponse<AiTutoringSessionVO> chat(@Valid @RequestBody AiTutorChatRequest request) {
        return ApiResponse.success(aiTutorService.chat(request));
    }

    @GetMapping("/sessions/me")
    @PreAuthorize("hasAnyAuthority('ai_tutor.chat_self','ai_tutor.chat.self')")
    public ApiResponse<List<AiTutoringSessionVO>> mySessions(@RequestParam(required = false) Long knowledgePointId) {
        return ApiResponse.success(aiTutorService.mySessions(knowledgePointId));
    }

    @PostMapping("/sessions/{id}/feedback")
    @PreAuthorize("hasAnyAuthority('ai_tutor.chat_self','ai_tutor.chat.self')")
    public ApiResponse<AiTutoringSessionVO> feedback(@PathVariable Long id, @Valid @RequestBody AiTutorFeedbackRequest request) {
        return ApiResponse.success(aiTutorService.feedback(id, request));
    }
}
