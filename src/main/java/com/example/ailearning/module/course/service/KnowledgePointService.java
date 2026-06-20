package com.example.ailearning.module.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.dto.KnowledgePointRequest;
import com.example.ailearning.module.course.entity.CourseKnowledgePoint;
import com.example.ailearning.module.course.entity.KnowledgePointRelation;
import com.example.ailearning.module.course.mapper.CourseKnowledgePointMapper;
import com.example.ailearning.module.course.mapper.KnowledgePointRelationMapper;
import com.example.ailearning.module.course.vo.CourseGraphVO;
import com.example.ailearning.module.course.vo.KnowledgePointRelationVO;
import com.example.ailearning.module.course.vo.KnowledgePointVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KnowledgePointService {
    private final CourseService courseService;
    private final CourseKnowledgePointMapper knowledgePointMapper;
    private final KnowledgePointRelationMapper relationMapper;

    public KnowledgePointService(
            CourseService courseService,
            CourseKnowledgePointMapper knowledgePointMapper,
            KnowledgePointRelationMapper relationMapper
    ) {
        this.courseService = courseService;
        this.knowledgePointMapper = knowledgePointMapper;
        this.relationMapper = relationMapper;
    }

    public List<KnowledgePointVO> listByCourse(Long courseId) {
        courseService.getEntity(courseId);
        return knowledgePointMapper.selectList(new LambdaQueryWrapper<CourseKnowledgePoint>()
                        .eq(CourseKnowledgePoint::getCourseId, courseId)
                        .isNull(CourseKnowledgePoint::getDeletedAt)
                        .orderByAsc(CourseKnowledgePoint::getSortOrder)
                        .orderByAsc(CourseKnowledgePoint::getId))
                .stream().map(this::toVO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public KnowledgePointVO create(Long courseId, KnowledgePointRequest request) {
        courseService.getEntity(courseId);
        CourseKnowledgePoint point = new CourseKnowledgePoint();
        fill(point, request);
        point.setCourseId(courseId);
        point.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        knowledgePointMapper.insert(point);
        return toVO(point);
    }

    @Transactional(rollbackFor = Exception.class)
    public KnowledgePointVO update(Long id, KnowledgePointRequest request) {
        CourseKnowledgePoint point = getEntity(id);
        fill(point, request);
        knowledgePointMapper.updateById(point);
        return toVO(point);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        CourseKnowledgePoint point = getEntity(id);
        point.setDeletedAt(DeleteConstants.now());
        point.setStatus("deleted");
        knowledgePointMapper.updateById(point);
    }

    public CourseGraphVO graph(Long courseId) {
        List<CourseKnowledgePoint> points = knowledgePointMapper.selectList(new LambdaQueryWrapper<CourseKnowledgePoint>()
                .eq(CourseKnowledgePoint::getCourseId, courseId)
                .isNull(CourseKnowledgePoint::getDeletedAt)
                .orderByAsc(CourseKnowledgePoint::getSortOrder)
                .orderByAsc(CourseKnowledgePoint::getId));
        Set<Long> pointIds = points.stream().map(CourseKnowledgePoint::getId).collect(Collectors.toSet());
        List<KnowledgePointRelation> relations = pointIds.isEmpty()
                ? List.of()
                : relationMapper.selectList(new LambdaQueryWrapper<KnowledgePointRelation>()
                .in(KnowledgePointRelation::getSourceKnowledgePointId, pointIds)
                .in(KnowledgePointRelation::getTargetKnowledgePointId, pointIds)
                .isNull(KnowledgePointRelation::getDeletedAt)
                .orderByAsc(KnowledgePointRelation::getId));

        CourseGraphVO graph = new CourseGraphVO();
        graph.setNodes(points.stream().map(this::toVO).toList());
        graph.setEdges(relations.stream().map(this::toRelationVO).toList());
        return graph;
    }

    public CourseKnowledgePoint getEntity(Long id) {
        CourseKnowledgePoint point = knowledgePointMapper.selectById(id);
        if (point == null || point.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识点不存在");
        }
        return point;
    }

    private void fill(CourseKnowledgePoint point, KnowledgePointRequest request) {
        point.setParentId(request.getParentId());
        point.setName(request.getName());
        point.setDescription(request.getDescription());
        point.setDifficultyLevel(request.getDifficultyLevel());
        point.setSortOrder(request.getSortOrder());
        point.setStatus(request.getStatus());
    }

    KnowledgePointVO toVO(CourseKnowledgePoint point) {
        KnowledgePointVO vo = new KnowledgePointVO();
        vo.setId(point.getId());
        vo.setCourseId(point.getCourseId());
        vo.setParentId(point.getParentId());
        vo.setName(point.getName());
        vo.setDescription(point.getDescription());
        vo.setDifficultyLevel(point.getDifficultyLevel());
        vo.setSortOrder(point.getSortOrder());
        vo.setStatus(point.getStatus());
        return vo;
    }

    KnowledgePointRelationVO toRelationVO(KnowledgePointRelation relation) {
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
