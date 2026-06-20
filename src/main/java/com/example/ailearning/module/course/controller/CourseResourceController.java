package com.example.ailearning.module.course.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.course.dto.CourseResourceRequest;
import com.example.ailearning.module.course.service.CourseResourceService;
import com.example.ailearning.module.course.vo.CourseResourceVO;
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

@RestController
@RequestMapping("/api/v1/course-resources")
public class CourseResourceController {
    private final CourseResourceService resourceService;

    public CourseResourceController(CourseResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public ApiResponse<PageResult<CourseResourceVO>> page(
            PageQuery query,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long knowledgePointId,
            @RequestParam(required = false) String resourceType
    ) {
        return ApiResponse.success(resourceService.page(query, courseId, knowledgePointId, resourceType));
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResourceVO> get(@PathVariable Long id) {
        return ApiResponse.success(resourceService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('course_resource.upload')")
    public ApiResponse<CourseResourceVO> create(@Valid @RequestBody CourseResourceRequest request) {
        return ApiResponse.success(resourceService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('course_resource.upload')")
    public ApiResponse<CourseResourceVO> update(@PathVariable Long id, @Valid @RequestBody CourseResourceRequest request) {
        return ApiResponse.success(resourceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('course_resource.upload')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        resourceService.softDelete(id);
        return ApiResponse.success(null);
    }
}
