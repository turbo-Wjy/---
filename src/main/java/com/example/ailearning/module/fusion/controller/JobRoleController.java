package com.example.ailearning.module.fusion.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.fusion.dto.JobCapabilityRequest;
import com.example.ailearning.module.fusion.dto.JobRoleRequest;
import com.example.ailearning.module.fusion.service.JobRoleService;
import com.example.ailearning.module.fusion.vo.JobCapabilityVO;
import com.example.ailearning.module.fusion.vo.JobRoleVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-roles")
public class JobRoleController {
    private final JobRoleService jobRoleService;

    public JobRoleController(JobRoleService jobRoleService) {
        this.jobRoleService = jobRoleService;
    }

    @GetMapping
    public ApiResponse<PageResult<JobRoleVO>> page(PageQuery query, @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(jobRoleService.page(query, majorId));
    }

    @GetMapping("/{id}")
    public ApiResponse<JobRoleVO> get(@PathVariable Long id) {
        return ApiResponse.success(jobRoleService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('job_role.manage.major')")
    public ApiResponse<JobRoleVO> create(@Valid @RequestBody JobRoleRequest request) {
        return ApiResponse.success(jobRoleService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('job_role.manage.major')")
    public ApiResponse<JobRoleVO> update(@PathVariable Long id, @Valid @RequestBody JobRoleRequest request) {
        return ApiResponse.success(jobRoleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('job_role.manage.major')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        jobRoleService.softDelete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/capabilities")
    public ApiResponse<List<JobCapabilityVO>> capabilities(@PathVariable Long id) {
        return ApiResponse.success(jobRoleService.listCapabilities(id));
    }

    @PostMapping("/{id}/capabilities")
    @PreAuthorize("hasAuthority('job_capability.manage.major')")
    public ApiResponse<JobCapabilityVO> createCapability(@PathVariable Long id, @Valid @RequestBody JobCapabilityRequest request) {
        return ApiResponse.success(jobRoleService.createCapability(id, request));
    }
}
