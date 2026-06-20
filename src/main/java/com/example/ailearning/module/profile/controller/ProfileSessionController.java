package com.example.ailearning.module.profile.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.profile.dto.ProfileExtractRequest;
import com.example.ailearning.module.profile.dto.ProfileSessionCreateRequest;
import com.example.ailearning.module.profile.dto.ProfileSessionMessageRequest;
import com.example.ailearning.module.profile.service.ProfileService;
import com.example.ailearning.module.profile.vo.ProfileSessionVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile-sessions")
public class ProfileSessionController {
    private final ProfileService profileService;

    public ProfileSessionController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('profile.session.create.self')")
    public ApiResponse<ProfileSessionVO> create(@RequestBody ProfileSessionCreateRequest request) {
        return ApiResponse.success(profileService.createSession(request));
    }

    @PostMapping("/{id}/messages")
    @PreAuthorize("hasAuthority('profile.session.create.self')")
    public ApiResponse<ProfileSessionVO> addMessage(@PathVariable Long id, @Valid @RequestBody ProfileSessionMessageRequest request) {
        return ApiResponse.success(profileService.addMessage(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('profile.session.create.self')")
    public ApiResponse<ProfileSessionVO> get(@PathVariable Long id) {
        return ApiResponse.success(profileService.getSession(id));
    }

    @PostMapping("/{id}/extract")
    @PreAuthorize("hasAuthority('profile.session.create.self')")
    public ApiResponse<ProfileSessionVO> extract(@PathVariable Long id, @RequestBody ProfileExtractRequest request) {
        return ApiResponse.success(profileService.extract(id, request));
    }
}
