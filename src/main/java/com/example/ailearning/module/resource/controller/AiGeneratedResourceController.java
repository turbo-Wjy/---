package com.example.ailearning.module.resource.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.resource.service.ResourcePackageService;
import com.example.ailearning.module.resource.vo.AiGeneratedResourceVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resources")
public class AiGeneratedResourceController {
    private final ResourcePackageService resourcePackageService;

    public AiGeneratedResourceController(ResourcePackageService resourcePackageService) {
        this.resourcePackageService = resourcePackageService;
    }

    @GetMapping("/{id}")
    public ApiResponse<AiGeneratedResourceVO> get(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.getResource(id));
    }

    @GetMapping("/{id}/preview")
    public ApiResponse<AiGeneratedResourceVO> preview(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.getResource(id));
    }

    @GetMapping("/{id}/download")
    public ApiResponse<AiGeneratedResourceVO> download(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.getResource(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('resource_package.generate.self')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        resourcePackageService.deleteResource(id);
        return ApiResponse.success(null);
    }
}
