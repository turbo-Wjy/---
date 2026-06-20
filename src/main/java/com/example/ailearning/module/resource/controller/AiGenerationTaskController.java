package com.example.ailearning.module.resource.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.resource.dto.AiGenerationTaskRequest;
import com.example.ailearning.module.resource.service.ResourcePackageService;
import com.example.ailearning.module.resource.vo.AiGenerationTaskLogVO;
import com.example.ailearning.module.resource.vo.AiGenerationTaskVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai-generation-tasks")
public class AiGenerationTaskController {
    private final ResourcePackageService resourcePackageService;

    public AiGenerationTaskController(ResourcePackageService resourcePackageService) {
        this.resourcePackageService = resourcePackageService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('resource_package.generate.self')")
    public ApiResponse<AiGenerationTaskVO> create(@RequestBody AiGenerationTaskRequest request) {
        return ApiResponse.success(resourcePackageService.createGenerationTask(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<AiGenerationTaskVO> get(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.getTask(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('resource_package.generate.self')")
    public ApiResponse<AiGenerationTaskVO> cancel(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.cancelTask(id));
    }

    @PostMapping("/{id}/retry")
    @PreAuthorize("hasAuthority('resource_package.generate.self')")
    public ApiResponse<AiGenerationTaskVO> retry(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.retryTask(id));
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<AiGenerationTaskLogVO>> logs(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.taskLogs(id));
    }
}
