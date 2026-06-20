package com.example.ailearning.module.role.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.role.dto.RolePermissionRequest;
import com.example.ailearning.module.role.dto.RoleRequest;
import com.example.ailearning.module.role.service.RoleService;
import com.example.ailearning.module.role.vo.RoleVO;
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
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role.manage')")
    public ApiResponse<PageResult<RoleVO>> page(PageQuery query) {
        return ApiResponse.success(roleService.page(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role.manage')")
    public ApiResponse<RoleVO> get(@PathVariable Long id) {
        return ApiResponse.success(roleService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role.manage')")
    public ApiResponse<RoleVO> create(@Valid @RequestBody RoleRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role.manage')")
    public ApiResponse<RoleVO> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ApiResponse.success(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.softDelete(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('permission.manage')")
    public ApiResponse<Void> updatePermissions(@PathVariable Long id, @Valid @RequestBody RolePermissionRequest request) {
        roleService.updatePermissions(id, request);
        return ApiResponse.success(null);
    }
}
