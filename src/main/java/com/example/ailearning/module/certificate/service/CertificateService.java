package com.example.ailearning.module.certificate.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.certificate.dto.CertificateRequest;
import com.example.ailearning.module.certificate.dto.CertificateResultRequest;
import com.example.ailearning.module.certificate.entity.Certificate;
import com.example.ailearning.module.certificate.entity.CertificateResult;
import com.example.ailearning.module.certificate.mapper.CertificateMapper;
import com.example.ailearning.module.certificate.mapper.CertificateResultMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificateService {
    private final CertificateMapper certificateMapper;
    private final CertificateResultMapper resultMapper;

    public CertificateService(CertificateMapper certificateMapper, CertificateResultMapper resultMapper) {
        this.certificateMapper = certificateMapper;
        this.resultMapper = resultMapper;
    }

    public List<Certificate> list(Long majorId) {
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<Certificate>()
                .isNull(Certificate::getDeletedAt)
                .orderByDesc(Certificate::getCreatedAt);
        if (majorId != null) {
            wrapper.eq(Certificate::getMajorId, majorId);
        }
        return certificateMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public Certificate create(CertificateRequest request) {
        Long userId = CurrentUserHolder.getRequired().getUserId();
        Certificate certificate = new Certificate();
        certificate.setMajorId(request.getMajorId());
        certificate.setCertificateName(request.getCertificateName());
        certificate.setRequirementLevel(request.getRequirementLevel());
        certificate.setGraduationRequired(Boolean.TRUE.equals(request.getGraduationRequired()));
        certificate.setResourceUrl(request.getResourceUrl());
        certificate.setImportedBy(userId);
        certificate.setStatus("active");
        certificate.setCreatedBy(userId);
        certificateMapper.insert(certificate);
        return certificate;
    }

    public List<CertificateResult> listResults(String reviewStatus) {
        LambdaQueryWrapper<CertificateResult> wrapper = new LambdaQueryWrapper<CertificateResult>()
                .isNull(CertificateResult::getDeletedAt)
                .orderByDesc(CertificateResult::getCreatedAt);
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(CertificateResult::getReviewStatus, reviewStatus);
        }
        return resultMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public CertificateResult submitResult(CertificateResultRequest request) {
        CertificateResult result = new CertificateResult();
        result.setCertificateId(request.getCertificateId());
        result.setStudentId(request.getStudentId());
        result.setCertificateNo(request.getCertificateNo());
        result.setIssuedAt(request.getIssuedAt());
        result.setProofFileUrl(request.getProofFileUrl());
        result.setReviewStatus("pending");
        result.setSubmittedAt(LocalDateTime.now());
        result.setStatus("pending");
        result.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        resultMapper.insert(result);
        return result;
    }
}
