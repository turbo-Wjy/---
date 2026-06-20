package com.example.ailearning.module.resource.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.resource.dto.ResourcePackageReviewRequest;
import com.example.ailearning.module.resource.service.ResourcePackageService;
import com.example.ailearning.module.resource.vo.ResourcePackageVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resource-packages")
public class ResourcePackageController {
    private final ResourcePackageService resourcePackageService;

    public ResourcePackageController(ResourcePackageService resourcePackageService) {
        this.resourcePackageService = resourcePackageService;
    }

    @GetMapping
    public ApiResponse<PageResult<ResourcePackageVO>> page(
            PageQuery query,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String reviewStatus
    ) {
        return ApiResponse.success(resourcePackageService.pagePackages(query, studentId, reviewStatus));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourcePackageVO> get(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.getPackage(id));
    }

    @PostMapping("/{id}/submit-review")
    @PreAuthorize("hasAuthority('resource_package.generate.self')")
    public ApiResponse<ResourcePackageVO> submitReview(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.submitReview(id));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('resource_package.review.assigned')")
    public ApiResponse<ResourcePackageVO> review(@PathVariable Long id, @Valid @RequestBody ResourcePackageReviewRequest request) {
        return ApiResponse.success(resourcePackageService.review(id, request));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('resource_package.publish.teacher')")
    public ApiResponse<ResourcePackageVO> publish(@PathVariable Long id) {
        return ApiResponse.success(resourcePackageService.publish(id));
    }
}
