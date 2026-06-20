package com.example.ailearning.module.profile.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.profile.dto.ProfileConfirmRequest;
import com.example.ailearning.module.profile.service.ProfileService;
import com.example.ailearning.module.profile.vo.LearningProfileVO;
import com.example.ailearning.module.profile.vo.ProfileSessionVO;
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
@RequestMapping("/api/v1/learning-profiles")
public class LearningProfileController {
    private final ProfileService profileService;

    public LearningProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/me/confirm")
    @PreAuthorize("hasAuthority('profile.confirm.self')")
    public ApiResponse<LearningProfileVO> confirm(@Valid @RequestBody ProfileConfirmRequest request) {
        return ApiResponse.success(profileService.confirm(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('profile.view.self')")
    public ApiResponse<LearningProfileVO> me() {
        return ApiResponse.success(profileService.myProfile());
    }

    @GetMapping("/me/versions")
    @PreAuthorize("hasAuthority('profile.view.self')")
    public ApiResponse<List<LearningProfileVO>> versions() {
        return ApiResponse.success(profileService.myVersions());
    }

    @GetMapping("/me/evidence")
    @PreAuthorize("hasAuthority('profile.view.self')")
    public ApiResponse<List<ProfileSessionVO>> evidence() {
        return ApiResponse.success(profileService.myEvidence());
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyAuthority('profile.view.assigned','profile.view.major')")
    public ApiResponse<LearningProfileVO> studentProfile(@PathVariable Long studentId) {
        return ApiResponse.success(profileService.studentProfile(studentId));
    }
}
