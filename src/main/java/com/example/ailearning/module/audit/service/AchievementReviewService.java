package com.example.ailearning.module.audit.service;

import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.module.audit.dto.ReviewRequest;
import com.example.ailearning.module.certificate.entity.CertificateResult;
import com.example.ailearning.module.certificate.mapper.CertificateResultMapper;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.mapper.CompetitionResultMapper;
import com.example.ailearning.module.project.entity.ProjectDeliverable;
import com.example.ailearning.module.project.mapper.ProjectDeliverableMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AchievementReviewService {
    private final CompetitionResultMapper competitionResultMapper;
    private final CertificateResultMapper certificateResultMapper;
    private final ProjectDeliverableMapper projectDeliverableMapper;
    private final AuditService auditService;

    public AchievementReviewService(
            CompetitionResultMapper competitionResultMapper,
            CertificateResultMapper certificateResultMapper,
            ProjectDeliverableMapper projectDeliverableMapper,
            AuditService auditService
    ) {
        this.competitionResultMapper = competitionResultMapper;
        this.certificateResultMapper = certificateResultMapper;
        this.projectDeliverableMapper = projectDeliverableMapper;
        this.auditService = auditService;
    }

    @Transactional(rollbackFor = Exception.class)
    public CompetitionResult reviewCompetitionResult(Long id, ReviewRequest request) {
        validateResult(request.getResult());
        CompetitionResult result = competitionResultMapper.selectById(id);
        if (result == null || result.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "竞赛成果不存在");
        }
        result.setReviewStatus(request.getResult());
        result.setStatus(request.getResult());
        if ("approved".equals(request.getResult())) {
            result.setApprovedAt(LocalDateTime.now());
        }
        competitionResultMapper.updateById(result);
        auditService.review("competition_result", id, "competition_result_review", request.getResult(), request.getComment());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public CertificateResult reviewCertificateResult(Long id, ReviewRequest request) {
        validateResult(request.getResult());
        CertificateResult result = certificateResultMapper.selectById(id);
        if (result == null || result.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "证书成果不存在");
        }
        result.setReviewStatus(request.getResult());
        result.setStatus(request.getResult());
        if ("approved".equals(request.getResult())) {
            result.setApprovedAt(LocalDateTime.now());
        }
        certificateResultMapper.updateById(result);
        auditService.review("certificate_result", id, "certificate_result_review", request.getResult(), request.getComment());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProjectDeliverable reviewProjectDeliverable(Long id, ReviewRequest request) {
        validateResult(request.getResult());
        ProjectDeliverable deliverable = projectDeliverableMapper.selectById(id);
        if (deliverable == null || deliverable.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "项目交付物不存在");
        }
        deliverable.setReviewStatus(request.getResult());
        deliverable.setStatus(request.getResult());
        deliverable.setTeacherComment(request.getComment());
        projectDeliverableMapper.updateById(deliverable);
        auditService.review("project_deliverable", id, "project_deliverable_review", request.getResult(), request.getComment());
        return deliverable;
    }

    private void validateResult(String result) {
        if (!"approved".equals(result) && !"rejected".equals(result)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "审核结果只能是 approved 或 rejected");
        }
    }
}
