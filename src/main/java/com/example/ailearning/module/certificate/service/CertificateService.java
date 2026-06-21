package com.example.ailearning.module.certificate.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.base.mapper.MajorMapper;
import com.example.ailearning.module.certificate.dto.CertificateRequest;
import com.example.ailearning.module.certificate.entity.Certificate;
import com.example.ailearning.module.certificate.mapper.CertificateMapper;
import com.example.ailearning.module.certificate.vo.CertificateVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificateService {
    private final CertificateMapper certificateMapper;
    private final MajorMapper majorMapper;
    private final AuditService auditService;

    public CertificateService(CertificateMapper certificateMapper, MajorMapper majorMapper, AuditService auditService) {
        this.certificateMapper = certificateMapper;
        this.majorMapper = majorMapper;
        this.auditService = auditService;
    }

    public PageResult<CertificateVO> page(PageQuery query, Long majorId, Boolean graduationRequired) {
        Page<Certificate> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<Certificate>()
                .isNull(Certificate::getDeletedAt)
                .orderByDesc(Certificate::getCreatedAt);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Certificate::getCertificateName, query.getKeyword());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Certificate::getStatus, query.getStatus());
        }
        if (majorId != null) {
            wrapper.eq(Certificate::getMajorId, majorId);
        }
        if (graduationRequired != null) {
            wrapper.eq(Certificate::getGraduationRequired, graduationRequired);
        }
        Page<Certificate> result = certificateMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toVO).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    public CertificateVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public CertificateVO create(CertificateRequest request) {
        if (majorMapper.selectById(request.getMajorId()) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专业不存在");
        }
        Certificate certificate = new Certificate();
        fill(certificate, request);
        certificate.setImportedBy(CurrentUserHolder.getRequired().getUserId());
        certificate.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        certificateMapper.insert(certificate);
        auditService.operation("certificate_standard", "create_certificate", "certificate", certificate.getId(), "success", "导入证书标准");
        return toVO(certificate);
    }

    @Transactional(rollbackFor = Exception.class)
    public CertificateVO update(Long id, CertificateRequest request) {
        Certificate certificate = getEntity(id);
        fill(certificate, request);
        certificateMapper.updateById(certificate);
        auditService.operation("certificate_standard", "update_certificate", "certificate", certificate.getId(), "success", "更新证书标准");
        return toVO(certificate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Certificate certificate = getEntity(id);
        certificate.setDeletedAt(DeleteConstants.now());
        certificate.setStatus("deleted");
        certificateMapper.updateById(certificate);
        auditService.operation("certificate_standard", "delete_certificate", "certificate", certificate.getId(), "success", "删除证书标准");
    }

    public Certificate getEntity(Long id) {
        Certificate certificate = certificateMapper.selectById(id);
        if (certificate == null || certificate.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "证书标准不存在");
        }
        return certificate;
    }

    private void fill(Certificate certificate, CertificateRequest request) {
        certificate.setMajorId(request.getMajorId());
        certificate.setCertificateName(request.getCertificateName());
        certificate.setRequirementLevel(request.getRequirementLevel());
        certificate.setGraduationRequired(Boolean.TRUE.equals(request.getGraduationRequired()));
        certificate.setResourceUrl(request.getResourceUrl());
        certificate.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "active" : request.getStatus());
    }

    private CertificateVO toVO(Certificate certificate) {
        CertificateVO vo = new CertificateVO();
        vo.setId(certificate.getId());
        vo.setMajorId(certificate.getMajorId());
        vo.setCertificateName(certificate.getCertificateName());
        vo.setRequirementLevel(certificate.getRequirementLevel());
        vo.setGraduationRequired(certificate.getGraduationRequired());
        vo.setResourceUrl(certificate.getResourceUrl());
        vo.setImportedBy(certificate.getImportedBy());
        vo.setStatus(certificate.getStatus());
        vo.setCreatedAt(certificate.getCreatedAt());
        return vo;
    }
}
