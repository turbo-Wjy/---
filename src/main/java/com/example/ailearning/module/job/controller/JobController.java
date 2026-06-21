package com.example.ailearning.module.job.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.job.dto.JobApplicationRequest;
import com.example.ailearning.module.job.dto.JobApplicationReviewRequest;
import com.example.ailearning.module.job.dto.JobPostRequest;
import com.example.ailearning.module.job.dto.ResumeGenerateRequest;
import com.example.ailearning.module.job.entity.JobApplication;
import com.example.ailearning.module.job.entity.JobPost;
import com.example.ailearning.module.job.entity.Resume;
import com.example.ailearning.module.job.service.JobService;
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
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/job-posts")
    public ApiResponse<List<JobPost>> jobPosts(@RequestParam(required = false) Long majorId, @RequestParam(required = false) String reviewStatus) {
        return ApiResponse.success(jobService.listJobPosts(majorId, reviewStatus));
    }

    @PostMapping("/job-posts")
    @PreAuthorize("hasAuthority('job_post.create')")
    public ApiResponse<JobPost> createJobPost(@Valid @RequestBody JobPostRequest request) {
        return ApiResponse.success(jobService.createJobPost(request));
    }

    @PostMapping("/resumes/generate")
    @PreAuthorize("hasAuthority('resume.generate_ai')")
    public ApiResponse<Resume> generateResume(@RequestBody ResumeGenerateRequest request) {
        return ApiResponse.success(jobService.generateResume(request));
    }

    @GetMapping("/resumes/me")
    @PreAuthorize("hasAuthority('resume.generate_ai')")
    public ApiResponse<List<Resume>> myResumes() {
        return ApiResponse.success(jobService.myResumes());
    }

    @GetMapping("/job-applications")
    public ApiResponse<List<JobApplication>> jobApplications(@RequestParam(required = false) String applicationStatus) {
        return ApiResponse.success(jobService.listApplications(applicationStatus));
    }

    @PostMapping("/job-applications")
    @PreAuthorize("hasAuthority('job_application.submit')")
    public ApiResponse<JobApplication> submitApplication(@Valid @RequestBody JobApplicationRequest request) {
        return ApiResponse.success(jobService.submitApplication(request));
    }

    @PostMapping("/job-applications/{id}/teacher-review")
    @PreAuthorize("hasAnyAuthority('resume.review_group','job_post.review_major')")
    public ApiResponse<JobApplication> teacherReview(@PathVariable Long id, @Valid @RequestBody JobApplicationReviewRequest request) {
        return ApiResponse.success(jobService.teacherReview(id, request));
    }

    @PostMapping("/job-applications/{id}/enterprise-review")
    @PreAuthorize("hasAuthority('resume.review_enterprise')")
    public ApiResponse<JobApplication> enterpriseReview(@PathVariable Long id, @Valid @RequestBody JobApplicationReviewRequest request) {
        return ApiResponse.success(jobService.enterpriseReview(id, request));
    }

    @PostMapping("/job-applications/{id}/recommend")
    @PreAuthorize("hasAuthority('resume.recommend_company')")
    public ApiResponse<JobApplication> recommend(@PathVariable Long id) {
        return ApiResponse.success(jobService.recommend(id));
    }
}
