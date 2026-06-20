package com.example.ailearning.module.auth.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.auth.dto.ChangePasswordRequest;
import com.example.ailearning.module.auth.dto.LoginRequest;
import com.example.ailearning.module.auth.service.AuthService;
import com.example.ailearning.module.auth.vo.LoginResponse;
import com.example.ailearning.module.user.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/force-change-password")
    public ApiResponse<Void> forceChangePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.forceChangePassword(request.getNewPassword());
        return ApiResponse.success(null);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.forceChangePassword(request.getNewPassword());
        return ApiResponse.success(null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(authService.currentUser());
    }

    @GetMapping("/me/menus")
    public ApiResponse<List<String>> menus() {
        return ApiResponse.success(authService.currentMenus());
    }

    @GetMapping("/me/permissions")
    public ApiResponse<List<String>> permissions() {
        return ApiResponse.success(authService.currentPermissions());
    }
}
