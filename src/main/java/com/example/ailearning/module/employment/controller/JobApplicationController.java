package com.example.ailearning.module.employment.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.achievement.dto.ReviewRequest;
import com.example.ailearning.module.employment.dto.EnterpriseReviewRequest;
import com.example.ailearning.module.employment.dto.JobApplicationSubmitRequest;
import com.example.ailearning.module.employment.service.JobApplicationService;
import com.example.ailearning.module.employment.vo.JobApplicationVO;
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
@RequestMapping("/api/v1/job-applications")
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('resume.review_group','resume.review_enterprise','job_application.submit')")
    public ApiResponse<PageResult<JobApplicationVO>> page(
            PageQuery query,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String applicationStatus
    ) {
        return ApiResponse.success(jobApplicationService.page(query, studentId, jobId, applicationStatus));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('job_application.submit')")
    public ApiResponse<JobApplicationVO> submit(@Valid @RequestBody JobApplicationSubmitRequest request) {
        return ApiResponse.success(jobApplicationService.submit(request));
    }

    @PostMapping("/{id}/teacher-review")
    @PreAuthorize("hasAuthority('resume.review_group')")
    public ApiResponse<JobApplicationVO> teacherReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(jobApplicationService.teacherReview(id, request));
    }

    @PostMapping("/{id}/enterprise-review")
    @PreAuthorize("hasAuthority('resume.review_enterprise')")
    public ApiResponse<JobApplicationVO> enterpriseReview(@PathVariable Long id, @Valid @RequestBody EnterpriseReviewRequest request) {
        return ApiResponse.success(jobApplicationService.enterpriseReview(id, request));
    }

    @PostMapping("/{id}/recommend")
    @PreAuthorize("hasAuthority('resume.recommend_company')")
    public ApiResponse<JobApplicationVO> recommend(@PathVariable Long id, @Valid @RequestBody EnterpriseReviewRequest request) {
        return ApiResponse.success(jobApplicationService.recommend(id, request));
    }
}
