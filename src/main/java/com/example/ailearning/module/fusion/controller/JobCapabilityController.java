package com.example.ailearning.module.fusion.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.fusion.dto.JobCapabilityRequest;
import com.example.ailearning.module.fusion.service.JobRoleService;
import com.example.ailearning.module.fusion.vo.JobCapabilityVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/job-capabilities")
public class JobCapabilityController {
    private final JobRoleService jobRoleService;

    public JobCapabilityController(JobRoleService jobRoleService) {
        this.jobRoleService = jobRoleService;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('job_capability.manage.major')")
    public ApiResponse<JobCapabilityVO> update(@PathVariable Long id, @Valid @RequestBody JobCapabilityRequest request) {
        return ApiResponse.success(jobRoleService.updateCapability(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('job_capability.manage.major')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        jobRoleService.softDeleteCapability(id);
        return ApiResponse.success(null);
    }
}
