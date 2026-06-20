package com.example.ailearning.module.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.dto.KnowledgePointRelationRequest;
import com.example.ailearning.module.course.entity.KnowledgePointRelation;
import com.example.ailearning.module.course.mapper.KnowledgePointRelationMapper;
import com.example.ailearning.module.course.vo.KnowledgePointRelationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KnowledgePointRelationService {
    private final KnowledgePointService knowledgePointService;
    private final KnowledgePointRelationMapper relationMapper;

    public KnowledgePointRelationService(KnowledgePointService knowledgePointService, KnowledgePointRelationMapper relationMapper) {
        this.knowledgePointService = knowledgePointService;
        this.relationMapper = relationMapper;
    }

    public List<KnowledgePointRelationVO> listByKnowledgePoint(Long knowledgePointId) {
        return relationMapper.selectList(new LambdaQueryWrapper<KnowledgePointRelation>()
                        .and(w -> w.eq(KnowledgePointRelation::getSourceKnowledgePointId, knowledgePointId)
                                .or()
                                .eq(KnowledgePointRelation::getTargetKnowledgePointId, knowledgePointId))
                        .isNull(KnowledgePointRelation::getDeletedAt)
                        .orderByAsc(KnowledgePointRelation::getId))
                .stream().map(this::toVO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public KnowledgePointRelationVO create(KnowledgePointRelationRequest request) {
        knowledgePointService.getEntity(request.getSourceKnowledgePointId());
        knowledgePointService.getEntity(request.getTargetKnowledgePointId());
        if (request.getSourceKnowledgePointId().equals(request.getTargetKnowledgePointId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "知识点不能关联自身");
        }
        boolean exists = relationMapper.exists(new LambdaQueryWrapper<KnowledgePointRelation>()
                .eq(KnowledgePointRelation::getSourceKnowledgePointId, request.getSourceKnowledgePointId())
                .eq(KnowledgePointRelation::getTargetKnowledgePointId, request.getTargetKnowledgePointId())
                .eq(KnowledgePointRelation::getRelationType, request.getRelationType())
                .isNull(KnowledgePointRelation::getDeletedAt));
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT, "知识点关系已存在");
        }
        KnowledgePointRelation relation = new KnowledgePointRelation();
        relation.setSourceKnowledgePointId(request.getSourceKnowledgePointId());
        relation.setTargetKnowledgePointId(request.getTargetKnowledgePointId());
        relation.setRelationType(request.getRelationType());
        relation.setWeight(request.getWeight());
        relation.setDescription(request.getDescription());
        relation.setStatus(request.getStatus());
        relation.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        relationMapper.insert(relation);
        return toVO(relation);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        KnowledgePointRelation relation = relationMapper.selectById(id);
        if (relation == null || relation.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识点关系不存在");
        }
        relation.setDeletedAt(DeleteConstants.now());
        relation.setStatus("deleted");
        relationMapper.updateById(relation);
    }

    private KnowledgePointRelationVO toVO(KnowledgePointRelation relation) {
        KnowledgePointRelationVO vo = new KnowledgePointRelationVO();
        vo.setId(relation.getId());
        vo.setSourceKnowledgePointId(relation.getSourceKnowledgePointId());
        vo.setTargetKnowledgePointId(relation.getTargetKnowledgePointId());
        vo.setRelationType(relation.getRelationType());
        vo.setWeight(relation.getWeight());
        vo.setDescription(relation.getDescription());
        vo.setStatus(relation.getStatus());
        return vo;
    }
}
