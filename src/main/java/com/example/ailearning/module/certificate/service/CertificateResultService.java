package com.example.ailearning.module.certificate.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.achievement.dto.ReviewRequest;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.certificate.dto.CertificateResultRequest;
import com.example.ailearning.module.certificate.entity.CertificateResult;
import com.example.ailearning.module.certificate.mapper.CertificateResultMapper;
import com.example.ailearning.module.certificate.vo.CertificateResultVO;
import com.example.ailearning.module.profile.entity.ProfileUpdateLog;
import com.example.ailearning.module.profile.mapper.ProfileUpdateLogMapper;
import com.example.ailearning.module.student.service.StudentContextService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CertificateResultService {
    private final CertificateService certificateService;
    private final CertificateResultMapper resultMapper;
    private final StudentContextService studentContextService;
    private final ProfileUpdateLogMapper profileUpdateLogMapper;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public CertificateResultService(
            CertificateService certificateService,
            CertificateResultMapper resultMapper,
            StudentContextService studentContextService,
            ProfileUpdateLogMapper profileUpdateLogMapper,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.certificateService = certificateService;
        this.resultMapper = resultMapper;
        this.studentContextService = studentContextService;
        this.profileUpdateLogMapper = profileUpdateLogMapper;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    public PageResult<CertificateResultVO> page(PageQuery query, Long studentId, Long certificateId, String reviewStatus) {
        Page<CertificateResult> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<CertificateResult> wrapper = new LambdaQueryWrapper<CertificateResult>()
                .isNull(CertificateResult::getDeletedAt)
                .orderByDesc(CertificateResult::getCreatedAt);
        if (studentId != null) {
            wrapper.eq(CertificateResult::getStudentId, studentId);
        }
        if (certificateId != null) {
            wrapper.eq(CertificateResult::getCertificateId, certificateId);
        }
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(CertificateResult::getReviewStatus, reviewStatus);
        }
        Page<CertificateResult> result = resultMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toVO).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    public List<CertificateResultVO> myResults() {
        Long studentId = studentContextService.currentStudentIdRequired();
        return resultMapper.selectList(new LambdaQueryWrapper<CertificateResult>()
                        .eq(CertificateResult::getStudentId, studentId)
                        .isNull(CertificateResult::getDeletedAt)
                        .orderByDesc(CertificateResult::getCreatedAt))
                .stream().map(this::toVO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public CertificateResultVO submit(CertificateResultRequest request) {
        certificateService.getEntity(request.getCertificateId());
        Long studentId = studentContextService.currentStudentIdRequired();
        CertificateResult result = new CertificateResult();
        result.setCertificateId(request.getCertificateId());
        result.setStudentId(studentId);
        result.setCertificateNo(request.getCertificateNo());
        result.setIssuedAt(request.getIssuedAt());
        result.setProofFileUrl(request.getProofFileUrl());
        result.setReviewStatus("pending");
        result.setSubmittedAt(LocalDateTime.now());
        result.setStatus("pending");
        result.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        resultMapper.insert(result);
        auditService.operation("certificate_standard", "submit_certificate_result", "certificate_result", result.getId(), "success", "学生上传证书成果并提交审核");
        return toVO(result);
    }

    @Transactional(rollbackFor = Exception.class)
    public CertificateResultVO review(Long id, ReviewRequest request) {
        CertificateResult result = getEntity(id);
        studentContextService.checkCanViewStudent(result.getStudentId());
        result.setReviewStatus(request.getReviewResult());
        result.setStatus(request.getReviewResult());
        result.setApprovedAt("approved".equals(request.getReviewResult()) ? LocalDateTime.now() : null);
        resultMapper.updateById(result);
        auditService.review("certificate_result", result.getId(), "group_teacher_review", request.getReviewResult(), request.getReviewComment());
        if ("approved".equals(request.getReviewResult())) {
            writeProfileUpdate(result);
        }
        return toVO(result);
    }

    private CertificateResult getEntity(Long id) {
        CertificateResult result = resultMapper.selectById(id);
        if (result == null || result.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "证书成果不存在");
        }
        return result;
    }

    private void writeProfileUpdate(CertificateResult result) {
        ProfileUpdateLog log = new ProfileUpdateLog();
        log.setStudentId(result.getStudentId());
        log.setSourceType("certificate_result");
        log.setSourceId(result.getId());
        log.setAfterSnapshotJson(toJson(Map.of(
                "certificateId", result.getCertificateId(),
                "certificateNo", result.getCertificateNo() == null ? "" : result.getCertificateNo(),
                "approvedAt", result.getApprovedAt().toString()
        )));
        log.setUpdatedReason("证书成果审核通过，进入学生画像数据源");
        log.setStatus("active");
        log.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        profileUpdateLogMapper.insert(log);
    }

    private CertificateResultVO toVO(CertificateResult result) {
        CertificateResultVO vo = new CertificateResultVO();
        vo.setId(result.getId());
        vo.setCertificateId(result.getCertificateId());
        vo.setStudentId(result.getStudentId());
        vo.setCertificateNo(result.getCertificateNo());
        vo.setIssuedAt(result.getIssuedAt());
        vo.setProofFileUrl(result.getProofFileUrl());
        vo.setReviewStatus(result.getReviewStatus());
        vo.setSubmittedAt(result.getSubmittedAt());
        vo.setApprovedAt(result.getApprovedAt());
        vo.setStatus(result.getStatus());
        vo.setCreatedAt(result.getCreatedAt());
        return vo;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "JSON 字段格式不正确");
        }
    }
}
