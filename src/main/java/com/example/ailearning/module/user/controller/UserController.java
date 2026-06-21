package com.example.ailearning.module.user.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.user.dto.ResetPasswordRequest;
import com.example.ailearning.module.user.dto.UserCreateRequest;
import com.example.ailearning.module.user.dto.UserPageQuery;
import com.example.ailearning.module.user.service.UserService;
import com.example.ailearning.module.user.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('account.manage')")
    public ApiResponse<PageResult<UserVO>> pageUsers(UserPageQuery query) {
        return ApiResponse.success(userService.pageUsers(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('account.manage')")
    public ApiResponse<UserVO> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserVO(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('account.manage')")
    public ApiResponse<UserVO> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @PostMapping("/bootstrap-admin")
    public ApiResponse<UserVO> bootstrapAdmin(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.bootstrapAdmin(request));
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('account.manage')")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(id, request.getNewPassword());
        return ApiResponse.success(null);
    }
}
