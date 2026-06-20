package com.example.ailearning.module.project.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.audit.dto.ReviewRequest;
import com.example.ailearning.module.audit.service.AchievementReviewService;
import com.example.ailearning.module.project.dto.ProjectDeliverableRequest;
import com.example.ailearning.module.project.dto.ProjectRequest;
import com.example.ailearning.module.project.entity.Project;
import com.example.ailearning.module.project.entity.ProjectDeliverable;
import com.example.ailearning.module.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProjectController {
    private final ProjectService projectService;
    private final AchievementReviewService reviewService;

    public ProjectController(ProjectService projectService, AchievementReviewService reviewService) {
        this.projectService = projectService;
        this.reviewService = reviewService;
    }

    @GetMapping("/projects")
    public ApiResponse<List<Project>> projects(@RequestParam(required = false) Long courseId) {
        return ApiResponse.success(projectService.list(courseId));
    }

    @PostMapping("/projects")
    @PreAuthorize("hasAuthority('project.manage.teacher')")
    public ApiResponse<Project> createProject(@Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success(projectService.create(request));
    }

    @GetMapping("/project-deliverables")
    public ApiResponse<List<ProjectDeliverable>> projectDeliverables(@RequestParam(required = false) String reviewStatus) {
        return ApiResponse.success(projectService.listDeliverables(reviewStatus));
    }

    @PostMapping("/project-deliverables")
    @PreAuthorize("hasAuthority('project.manage.teacher')")
    public ApiResponse<ProjectDeliverable> submitProjectDeliverable(@Valid @RequestBody ProjectDeliverableRequest request) {
        return ApiResponse.success(projectService.submitDeliverable(request));
    }

    @PostMapping("/project-deliverables/{id}/review")
    @PreAuthorize("hasAuthority('project_deliverable.review.assigned')")
    public ApiResponse<ProjectDeliverable> reviewProjectDeliverable(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(reviewService.reviewProjectDeliverable(id, request));
    }
}
