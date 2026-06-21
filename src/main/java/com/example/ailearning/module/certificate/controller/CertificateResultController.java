package com.example.ailearning.module.certificate.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.achievement.dto.ReviewRequest;
import com.example.ailearning.module.certificate.dto.CertificateResultRequest;
import com.example.ailearning.module.certificate.service.CertificateResultService;
import com.example.ailearning.module.certificate.vo.CertificateResultVO;
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
@RequestMapping("/api/v1/certificate-results")
public class CertificateResultController {
    private final CertificateResultService certificateResultService;

    public CertificateResultController(CertificateResultService certificateResultService) {
        this.certificateResultService = certificateResultService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('certificate_result.review_group','student_profile.view_major','student_profile.view_assigned')")
    public ApiResponse<PageResult<CertificateResultVO>> page(
            PageQuery query,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long certificateId,
            @RequestParam(required = false) String reviewStatus
    ) {
        return ApiResponse.success(certificateResultService.page(query, studentId, certificateId, reviewStatus));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('certificate_result.upload_self')")
    public ApiResponse<List<CertificateResultVO>> myResults() {
        return ApiResponse.success(certificateResultService.myResults());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('certificate_result.upload_self')")
    public ApiResponse<CertificateResultVO> submit(@Valid @RequestBody CertificateResultRequest request) {
        return ApiResponse.success(certificateResultService.submit(request));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('certificate_result.review_group')")
    public ApiResponse<CertificateResultVO> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(certificateResultService.review(id, request));
    }
}
