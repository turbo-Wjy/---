package com.example.ailearning.module.competition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.achievement.dto.ReviewRequest;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.competition.dto.CompetitionResultRequest;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.mapper.CompetitionResultMapper;
import com.example.ailearning.module.competition.vo.CompetitionResultVO;
import com.example.ailearning.module.profile.entity.ProfileUpdateLog;
import com.example.ailearning.module.profile.mapper.ProfileUpdateLogMapper;
import com.example.ailearning.module.student.mapper.StudentMapper;
import com.example.ailearning.module.teacher.entity.Teacher;
import com.example.ailearning.module.teacher.mapper.TeacherMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CompetitionResultService {
    private final CompetitionService competitionService;
    private final CompetitionResultMapper resultMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final ProfileUpdateLogMapper profileUpdateLogMapper;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public CompetitionResultService(
            CompetitionService competitionService,
            CompetitionResultMapper resultMapper,
            TeacherMapper teacherMapper,
            StudentMapper studentMapper,
            ProfileUpdateLogMapper profileUpdateLogMapper,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.competitionService = competitionService;
        this.resultMapper = resultMapper;
        this.teacherMapper = teacherMapper;
        this.studentMapper = studentMapper;
        this.profileUpdateLogMapper = profileUpdateLogMapper;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    public PageResult<CompetitionResultVO> page(PageQuery query, Long studentId, Long competitionId, String reviewStatus) {
        Page<CompetitionResult> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<CompetitionResult> wrapper = new LambdaQueryWrapper<CompetitionResult>()
                .isNull(CompetitionResult::getDeletedAt)
                .orderByDesc(CompetitionResult::getCreatedAt);
        if (studentId != null) {
            wrapper.eq(CompetitionResult::getStudentId, studentId);
        }
        if (competitionId != null) {
            wrapper.eq(CompetitionResult::getCompetitionId, competitionId);
        }
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(CompetitionResult::getReviewStatus, reviewStatus);
        }
        Page<CompetitionResult> result = resultMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toVO).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    public CompetitionResultVO submit(CompetitionResultRequest request) {
        competitionService.getEntity(request.getCompetitionId());
        if (studentMapper.selectById(request.getStudentId()) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生不存在");
        }
        Long coachTeacherId = request.getCoachTeacherId() == null ? currentTeacherIdRequired() : request.getCoachTeacherId();
        if (teacherMapper.selectById(coachTeacherId) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "带队老师不存在");
        }
        CompetitionResult result = new CompetitionResult();
        result.setCompetitionId(request.getCompetitionId());
        result.setStudentId(request.getStudentId());
        result.setCoachTeacherId(coachTeacherId);
        result.setAwardName(request.getAwardName());
        result.setProofFileUrl(request.getProofFileUrl());
        result.setReviewStatus("pending");
        result.setSubmittedAt(LocalDateTime.now());
        result.setStatus("pending");
        result.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        resultMapper.insert(result);
        auditService.operation("competition_growth", "submit_competition_result", "competition_result", result.getId(), "success", "上传竞赛成果并提交审核");
        return toVO(result);
    }

    @Transactional(rollbackFor = Exception.class)
    public CompetitionResultVO review(Long id, ReviewRequest request) {
        CompetitionResult result = getEntity(id);
        result.setReviewStatus(request.getReviewResult());
        result.setStatus(request.getReviewResult());
        result.setApprovedAt("approved".equals(request.getReviewResult()) ? LocalDateTime.now() : null);
        resultMapper.updateById(result);
        auditService.review("competition_result", result.getId(), "competition_admin_review", request.getReviewResult(), request.getReviewComment());
        if ("approved".equals(request.getReviewResult())) {
            writeProfileUpdate(result);
        }
        return toVO(result);
    }

    public CompetitionResult getEntity(Long id) {
        CompetitionResult result = resultMapper.selectById(id);
        if (result == null || result.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "竞赛成果不存在");
        }
        return result;
    }

    private Long currentTeacherIdRequired() {
        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUserId, CurrentUserHolder.getRequired().getUserId())
                .isNull(Teacher::getDeletedAt)
                .last("LIMIT 1"));
        if (teacher == null) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "当前账号未绑定教师信息");
        }
        return teacher.getId();
    }

    private void writeProfileUpdate(CompetitionResult result) {
        ProfileUpdateLog log = new ProfileUpdateLog();
        log.setStudentId(result.getStudentId());
        log.setSourceType("competition_result");
        log.setSourceId(result.getId());
        log.setAfterSnapshotJson(toJson(Map.of(
                "competitionId", result.getCompetitionId(),
                "awardName", result.getAwardName(),
                "approvedAt", result.getApprovedAt().toString()
        )));
        log.setUpdatedReason("竞赛成果审核通过，进入学生画像数据源");
        log.setStatus("active");
        log.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        profileUpdateLogMapper.insert(log);
    }

    private CompetitionResultVO toVO(CompetitionResult result) {
        CompetitionResultVO vo = new CompetitionResultVO();
        vo.setId(result.getId());
        vo.setCompetitionId(result.getCompetitionId());
        vo.setStudentId(result.getStudentId());
        vo.setCoachTeacherId(result.getCoachTeacherId());
        vo.setAwardName(result.getAwardName());
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
