package com.example.ailearning.module.fusion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.entity.CourseKnowledgePoint;
import com.example.ailearning.module.course.mapper.CourseKnowledgePointMapper;
import com.example.ailearning.module.fusion.dto.FusionRelationQuery;
import com.example.ailearning.module.fusion.dto.FusionRelationRequest;
import com.example.ailearning.module.fusion.entity.CertificateAssessmentPoint;
import com.example.ailearning.module.fusion.entity.CompetitionTask;
import com.example.ailearning.module.fusion.entity.FusionRelation;
import com.example.ailearning.module.fusion.entity.JobCapability;
import com.example.ailearning.module.fusion.mapper.CertificateAssessmentPointMapper;
import com.example.ailearning.module.fusion.mapper.CompetitionTaskMapper;
import com.example.ailearning.module.fusion.mapper.FusionRelationMapper;
import com.example.ailearning.module.fusion.vo.FusionRelationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FusionRelationService {
    public static final String TYPE_JOB_CAPABILITY = "job_capability";
    public static final String TYPE_COURSE_KNOWLEDGE_POINT = "course_knowledge_point";
    public static final String TYPE_COMPETITION_TASK = "competition_task";
    public static final String TYPE_CERTIFICATE_ASSESSMENT_POINT = "certificate_assessment_point";

    private final FusionRelationMapper relationMapper;
    private final JobRoleService jobRoleService;
    private final CourseKnowledgePointMapper knowledgePointMapper;
    private final CompetitionTaskMapper competitionTaskMapper;
    private final CertificateAssessmentPointMapper assessmentPointMapper;
    private final JsonValueService jsonValueService;

    public FusionRelationService(
            FusionRelationMapper relationMapper,
            JobRoleService jobRoleService,
            CourseKnowledgePointMapper knowledgePointMapper,
            CompetitionTaskMapper competitionTaskMapper,
            CertificateAssessmentPointMapper assessmentPointMapper,
            JsonValueService jsonValueService
    ) {
        this.relationMapper = relationMapper;
        this.jobRoleService = jobRoleService;
        this.knowledgePointMapper = knowledgePointMapper;
        this.competitionTaskMapper = competitionTaskMapper;
        this.assessmentPointMapper = assessmentPointMapper;
        this.jsonValueService = jsonValueService;
    }

    public PageResult<FusionRelationVO> page(FusionRelationQuery query) {
        Page<FusionRelation> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<FusionRelation> wrapper = new LambdaQueryWrapper<FusionRelation>()
                .isNull(FusionRelation::getDeletedAt)
                .orderByDesc(FusionRelation::getCreatedAt);
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(FusionRelation::getStatus, query.getStatus());
        }
        if (query.getSourceType() != null && !query.getSourceType().isBlank()) {
            wrapper.eq(FusionRelation::getSourceType, query.getSourceType());
        }
        if (query.getSourceId() != null) {
            wrapper.eq(FusionRelation::getSourceId, query.getSourceId());
        }
        if (query.getTargetType() != null && !query.getTargetType().isBlank()) {
            wrapper.eq(FusionRelation::getTargetType, query.getTargetType());
        }
        if (query.getTargetId() != null) {
            wrapper.eq(FusionRelation::getTargetId, query.getTargetId());
        }
        if (query.getRelationType() != null && !query.getRelationType().isBlank()) {
            wrapper.eq(FusionRelation::getRelationType, query.getRelationType());
        }
        Page<FusionRelation> result = relationMapper.selectPage(page, wrapper);
        List<FusionRelationVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    public FusionRelationVO create(FusionRelationRequest request) {
        validateTarget(request.getSourceType(), request.getSourceId());
        validateTarget(request.getTargetType(), request.getTargetId());
        if (request.getSourceType().equals(request.getTargetType()) && request.getSourceId().equals(request.getTargetId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "融合关系不能关联自身");
        }
        boolean exists = relationMapper.exists(new LambdaQueryWrapper<FusionRelation>()
                .eq(FusionRelation::getSourceType, request.getSourceType())
                .eq(FusionRelation::getSourceId, request.getSourceId())
                .eq(FusionRelation::getTargetType, request.getTargetType())
                .eq(FusionRelation::getTargetId, request.getTargetId())
                .eq(FusionRelation::getRelationType, request.getRelationType())
                .isNull(FusionRelation::getDeletedAt));
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT, "融合关系已存在");
        }
        FusionRelation relation = new FusionRelation();
        fill(relation, request);
        relation.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        relationMapper.insert(relation);
        return toVO(relation);
    }

    @Transactional(rollbackFor = Exception.class)
    public FusionRelationVO update(Long id, FusionRelationRequest request) {
        FusionRelation relation = getEntity(id);
        validateTarget(request.getSourceType(), request.getSourceId());
        validateTarget(request.getTargetType(), request.getTargetId());
        fill(relation, request);
        relationMapper.updateById(relation);
        return toVO(relation);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        FusionRelation relation = getEntity(id);
        relation.setDeletedAt(DeleteConstants.now());
        relation.setStatus("deleted");
        relationMapper.updateById(relation);
    }

    public FusionRelation getEntity(Long id) {
        FusionRelation relation = relationMapper.selectById(id);
        if (relation == null || relation.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "融合关系不存在");
        }
        return relation;
    }

    public FusionRelationVO toVO(FusionRelation relation) {
        FusionRelationVO vo = new FusionRelationVO();
        vo.setId(relation.getId());
        vo.setSourceType(relation.getSourceType());
        vo.setSourceId(relation.getSourceId());
        vo.setTargetType(relation.getTargetType());
        vo.setTargetId(relation.getTargetId());
        vo.setRelationType(relation.getRelationType());
        vo.setWeight(relation.getWeight());
        vo.setDescription(relation.getDescription());
        vo.setEvidence(relation.getEvidenceJson());
        vo.setStatus(relation.getStatus());
        return vo;
    }

    private void fill(FusionRelation relation, FusionRelationRequest request) {
        relation.setSourceType(request.getSourceType());
        relation.setSourceId(request.getSourceId());
        relation.setTargetType(request.getTargetType());
        relation.setTargetId(request.getTargetId());
        relation.setRelationType(request.getRelationType());
        relation.setWeight(request.getWeight());
        relation.setDescription(request.getDescription());
        relation.setEvidenceJson(jsonValueService.toJson(request.getEvidence()));
        relation.setStatus(request.getStatus());
    }

    private void validateTarget(String type, Long id) {
        if (TYPE_JOB_CAPABILITY.equals(type)) {
            JobCapability ignored = jobRoleService.getCapabilityEntity(id);
            return;
        }
        if (TYPE_COURSE_KNOWLEDGE_POINT.equals(type)) {
            CourseKnowledgePoint point = knowledgePointMapper.selectById(id);
            if (point == null || point.getDeletedAt() != null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "课程知识点不存在");
            }
            return;
        }
        if (TYPE_COMPETITION_TASK.equals(type)) {
            CompetitionTask task = competitionTaskMapper.selectById(id);
            if (task == null || task.getDeletedAt() != null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "竞赛任务不存在");
            }
            return;
        }
        if (TYPE_CERTIFICATE_ASSESSMENT_POINT.equals(type)) {
            CertificateAssessmentPoint point = assessmentPointMapper.selectById(id);
            if (point == null || point.getDeletedAt() != null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "证书考核点不存在");
            }
            return;
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的融合对象类型：" + type);
    }
}
