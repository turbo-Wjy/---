package com.example.ailearning.module.employment.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.employment.dto.ResumeConfirmRequest;
import com.example.ailearning.module.employment.dto.ResumeGenerateRequest;
import com.example.ailearning.module.employment.service.ResumeService;
import com.example.ailearning.module.employment.vo.ResumeVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('resume.generate_ai')")
    public ApiResponse<ResumeVO> generate(@RequestBody ResumeGenerateRequest request) {
        return ApiResponse.success(resumeService.generate(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('resume.generate_ai')")
    public ApiResponse<List<ResumeVO>> myResumes() {
        return ApiResponse.success(resumeService.myResumes());
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('resume.generate_ai')")
    public ApiResponse<ResumeVO> confirm(@PathVariable Long id, @Valid @RequestBody ResumeConfirmRequest request) {
        return ApiResponse.success(resumeService.confirm(id, request));
    }
}
