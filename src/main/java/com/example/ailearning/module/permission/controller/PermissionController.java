package com.example.ailearning.module.permission.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.permission.dto.PermissionRequest;
import com.example.ailearning.module.permission.service.PermissionService;
import com.example.ailearning.module.permission.vo.PermissionVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission.manage')")
    public ApiResponse<PageResult<PermissionVO>> page(PageQuery query) {
        return ApiResponse.success(permissionService.page(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission.manage')")
    public ApiResponse<PermissionVO> get(@PathVariable Long id) {
        return ApiResponse.success(permissionService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission.manage')")
    public ApiResponse<PermissionVO> create(@Valid @RequestBody PermissionRequest request) {
        return ApiResponse.success(permissionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission.manage')")
    public ApiResponse<PermissionVO> update(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        return ApiResponse.success(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.softDelete(id);
        return ApiResponse.success(null);
    }
}
