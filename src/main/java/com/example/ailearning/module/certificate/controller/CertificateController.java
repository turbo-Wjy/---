package com.example.ailearning.module.certificate.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.audit.dto.ReviewRequest;
import com.example.ailearning.module.audit.service.AchievementReviewService;
import com.example.ailearning.module.certificate.dto.CertificateRequest;
import com.example.ailearning.module.certificate.dto.CertificateResultRequest;
import com.example.ailearning.module.certificate.entity.Certificate;
import com.example.ailearning.module.certificate.entity.CertificateResult;
import com.example.ailearning.module.certificate.service.CertificateService;
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
public class CertificateController {
    private final CertificateService certificateService;
    private final AchievementReviewService reviewService;

    public CertificateController(CertificateService certificateService, AchievementReviewService reviewService) {
        this.certificateService = certificateService;
        this.reviewService = reviewService;
    }

    @GetMapping("/certificates")
    public ApiResponse<List<Certificate>> certificates(@RequestParam(required = false) Long majorId) {
        return ApiResponse.success(certificateService.list(majorId));
    }

    @PostMapping("/certificates")
    @PreAuthorize("hasAnyAuthority('certificate_standard.manage_major','certificate_standard.import_major')")
    public ApiResponse<Certificate> createCertificate(@Valid @RequestBody CertificateRequest request) {
        return ApiResponse.success(certificateService.create(request));
    }

    @GetMapping("/certificate-results")
    public ApiResponse<List<CertificateResult>> certificateResults(@RequestParam(required = false) String reviewStatus) {
        return ApiResponse.success(certificateService.listResults(reviewStatus));
    }

    @PostMapping("/certificate-results")
    @PreAuthorize("hasAnyAuthority('quiz.practice','certificate_result.upload_self')")
    public ApiResponse<CertificateResult> submitCertificateResult(@Valid @RequestBody CertificateResultRequest request) {
        return ApiResponse.success(certificateService.submitResult(request));
    }

    @PostMapping("/certificate-results/{id}/review")
    @PreAuthorize("hasAuthority('certificate_result.review_group')")
    public ApiResponse<CertificateResult> reviewCertificateResult(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(reviewService.reviewCertificateResult(id, request));
    }
}
