package com.example.ailearning.module.certificate.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.certificate.dto.CertificateRequest;
import com.example.ailearning.module.certificate.service.CertificateService;
import com.example.ailearning.module.certificate.vo.CertificateVO;
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
@RequestMapping("/api/v1/certificates")
public class CertificateController {
    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public ApiResponse<PageResult<CertificateVO>> page(
            PageQuery query,
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) Boolean graduationRequired
    ) {
        return ApiResponse.success(certificateService.page(query, majorId, graduationRequired));
    }

    @GetMapping("/{id}")
    public ApiResponse<CertificateVO> get(@PathVariable Long id) {
        return ApiResponse.success(certificateService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('certificate_standard.import_major')")
    public ApiResponse<CertificateVO> create(@Valid @RequestBody CertificateRequest request) {
        return ApiResponse.success(certificateService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('certificate_standard.import_major')")
    public ApiResponse<CertificateVO> update(@PathVariable Long id, @Valid @RequestBody CertificateRequest request) {
        return ApiResponse.success(certificateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('certificate_standard.import_major')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        certificateService.softDelete(id);
        return ApiResponse.success(null);
    }
}
