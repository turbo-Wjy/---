package com.example.ailearning.module.employment.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.achievement.dto.ReviewRequest;
import com.example.ailearning.module.employment.dto.JobPostRequest;
import com.example.ailearning.module.employment.service.JobPostService;
import com.example.ailearning.module.employment.vo.JobPostVO;
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
@RequestMapping("/api/v1/job-posts")
public class JobPostController {
    private final JobPostService jobPostService;

    public JobPostController(JobPostService jobPostService) {
        this.jobPostService = jobPostService;
    }

    @GetMapping
    public ApiResponse<PageResult<JobPostVO>> page(PageQuery query, @RequestParam(required = false) Long majorId, @RequestParam(required = false) String reviewStatus) {
        return ApiResponse.success(jobPostService.page(query, majorId, reviewStatus));
    }

    @GetMapping("/{id}")
    public ApiResponse<JobPostVO> get(@PathVariable Long id) {
        return ApiResponse.success(jobPostService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('job_post.create')")
    public ApiResponse<JobPostVO> create(@Valid @RequestBody JobPostRequest request) {
        return ApiResponse.success(jobPostService.create(request));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('job_post.review_major')")
    public ApiResponse<JobPostVO> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(jobPostService.review(id, request));
    }
}
