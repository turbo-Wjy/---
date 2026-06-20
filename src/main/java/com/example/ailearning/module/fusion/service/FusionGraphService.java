package com.example.ailearning.module.fusion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.entity.CourseKnowledgePoint;
import com.example.ailearning.module.course.mapper.CourseKnowledgePointMapper;
import com.example.ailearning.module.fusion.entity.CertificateAssessmentPoint;
import com.example.ailearning.module.fusion.entity.CompetitionTask;
import com.example.ailearning.module.fusion.entity.FusionRelation;
import com.example.ailearning.module.fusion.entity.JobCapability;
import com.example.ailearning.module.fusion.entity.JobRole;
import com.example.ailearning.module.fusion.entity.StudentCapabilityScore;
import com.example.ailearning.module.fusion.mapper.CertificateAssessmentPointMapper;
import com.example.ailearning.module.fusion.mapper.CompetitionTaskMapper;
import com.example.ailearning.module.fusion.mapper.FusionRelationMapper;
import com.example.ailearning.module.fusion.mapper.JobCapabilityMapper;
import com.example.ailearning.module.fusion.mapper.JobRoleMapper;
import com.example.ailearning.module.fusion.mapper.StudentCapabilityScoreMapper;
import com.example.ailearning.module.fusion.vo.FusionEdgeVO;
import com.example.ailearning.module.fusion.vo.FusionGraphVO;
import com.example.ailearning.module.fusion.vo.FusionNodeVO;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.mapper.StudentMapper;
import com.example.ailearning.module.student.service.StudentContextService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FusionGraphService {
    private final JobRoleMapper jobRoleMapper;
    private final JobCapabilityMapper capabilityMapper;
    private final FusionRelationMapper relationMapper;
    private final CourseKnowledgePointMapper knowledgePointMapper;
    private final CompetitionTaskMapper competitionTaskMapper;
    private final CertificateAssessmentPointMapper assessmentPointMapper;
    private final StudentCapabilityScoreMapper scoreMapper;
    private final StudentMapper studentMapper;
    private final StudentContextService studentContextService;

    public FusionGraphService(
            JobRoleMapper jobRoleMapper,
            JobCapabilityMapper capabilityMapper,
            FusionRelationMapper relationMapper,
            CourseKnowledgePointMapper knowledgePointMapper,
            CompetitionTaskMapper competitionTaskMapper,
            CertificateAssessmentPointMapper assessmentPointMapper,
            StudentCapabilityScoreMapper scoreMapper,
            StudentMapper studentMapper,
            StudentContextService studentContextService
    ) {
        this.jobRoleMapper = jobRoleMapper;
        this.capabilityMapper = capabilityMapper;
        this.relationMapper = relationMapper;
        this.knowledgePointMapper = knowledgePointMapper;
        this.competitionTaskMapper = competitionTaskMapper;
        this.assessmentPointMapper = assessmentPointMapper;
        this.scoreMapper = scoreMapper;
        this.studentMapper = studentMapper;
        this.studentContextService = studentContextService;
    }

    public FusionGraphVO myGraph(Long jobRoleId) {
        Long studentId = currentStudentId();
        return studentGraph(studentId, jobRoleId, false);
    }

    public FusionGraphVO assignedStudentGraph(Long studentId, Long jobRoleId) {
        checkAssignedScope(studentId);
        return studentGraph(studentId, jobRoleId, true);
    }

    public FusionGraphVO jobGraph(Long jobRoleId) {
        JobRole role = getJobRole(jobRoleId);
        return buildJobRoleGraph(List.of(role), null);
    }

    public FusionGraphVO courseGraph(Long courseId) {
        List<CourseKnowledgePoint> points = knowledgePointMapper.selectList(new LambdaQueryWrapper<CourseKnowledgePoint>()
                .eq(CourseKnowledgePoint::getCourseId, courseId)
                .isNull(CourseKnowledgePoint::getDeletedAt)
                .orderByAsc(CourseKnowledgePoint::getSortOrder)
                .orderByAsc(CourseKnowledgePoint::getId));
        if (points.isEmpty()) {
            return emptyGraph();
        }

        Map<String, FusionNodeVO> nodes = new LinkedHashMap<>();
        List<FusionEdgeVO> edges = new ArrayList<>();
        Set<Long> pointIds = points.stream().map(CourseKnowledgePoint::getId).collect(Collectors.toSet());
        points.forEach(point -> putNode(nodes, node(
                FusionRelationService.TYPE_COURSE_KNOWLEDGE_POINT,
                point.getId(),
                point.getName(),
                point.getDescription()
        )));

        List<FusionRelation> relations = relationMapper.selectList(new LambdaQueryWrapper<FusionRelation>()
                .and(w -> w.eq(FusionRelation::getSourceType, FusionRelationService.TYPE_COURSE_KNOWLEDGE_POINT)
                        .in(FusionRelation::getSourceId, pointIds)
                        .or()
                        .eq(FusionRelation::getTargetType, FusionRelationService.TYPE_COURSE_KNOWLEDGE_POINT)
                        .in(FusionRelation::getTargetId, pointIds))
                .isNull(FusionRelation::getDeletedAt)
                .orderByDesc(FusionRelation::getWeight));
        appendRelations(nodes, edges, relations);
        return graph(nodes, edges, null);
    }

    private FusionGraphVO studentGraph(Long studentId, Long jobRoleId, boolean assignedView) {
        Student student = studentMapper.selectById(studentId);
        if (student == null || student.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生不存在");
        }
        if (jobRoleId != null) {
            return buildJobRoleGraph(List.of(getJobRole(jobRoleId)), studentId);
        }

        LambdaQueryWrapper<JobRole> wrapper = new LambdaQueryWrapper<JobRole>()
                .isNull(JobRole::getDeletedAt)
                .eq(JobRole::getStatus, "active")
                .orderByAsc(JobRole::getSortOrder)
                .orderByAsc(JobRole::getId);
        if (!assignedView && student.getMajorId() != null) {
            wrapper.and(w -> w.eq(JobRole::getMajorId, student.getMajorId()).or().isNull(JobRole::getMajorId));
        }
        List<JobRole> roles = jobRoleMapper.selectList(wrapper);
        return buildJobRoleGraph(roles, studentId);
    }

    private FusionGraphVO buildJobRoleGraph(List<JobRole> roles, Long studentId) {
        if (roles.isEmpty()) {
            return emptyGraph();
        }
        Map<String, FusionNodeVO> nodes = new LinkedHashMap<>();
        List<FusionEdgeVO> edges = new ArrayList<>();
        List<Long> roleIds = roles.stream().map(JobRole::getId).toList();
        roles.forEach(role -> putNode(nodes, node("job_role", role.getId(), role.getRoleName(), role.getDescription())));

        List<JobCapability> capabilities = capabilityMapper.selectList(new LambdaQueryWrapper<JobCapability>()
                .in(JobCapability::getJobRoleId, roleIds)
                .isNull(JobCapability::getDeletedAt)
                .orderByAsc(JobCapability::getSortOrder)
                .orderByAsc(JobCapability::getId));
        for (JobCapability capability : capabilities) {
            putNode(nodes, node(
                    FusionRelationService.TYPE_JOB_CAPABILITY,
                    capability.getId(),
                    capability.getCapabilityName(),
                    capability.getDescription()
            ));
            edges.add(edge("job_role", capability.getJobRoleId(), FusionRelationService.TYPE_JOB_CAPABILITY, capability.getId(), "contains", capability.getWeight(), "岗位模型包含该能力点"));
        }

        List<Long> capabilityIds = capabilities.stream().map(JobCapability::getId).toList();
        if (!capabilityIds.isEmpty()) {
            List<FusionRelation> relations = relationMapper.selectList(new LambdaQueryWrapper<FusionRelation>()
                    .and(w -> w.eq(FusionRelation::getSourceType, FusionRelationService.TYPE_JOB_CAPABILITY)
                            .in(FusionRelation::getSourceId, capabilityIds)
                            .or()
                            .eq(FusionRelation::getTargetType, FusionRelationService.TYPE_JOB_CAPABILITY)
                            .in(FusionRelation::getTargetId, capabilityIds))
                    .isNull(FusionRelation::getDeletedAt)
                    .orderByDesc(FusionRelation::getWeight));
            appendRelations(nodes, edges, relations);
        }
        return graph(nodes, edges, studentId);
    }

    private void appendRelations(Map<String, FusionNodeVO> nodes, List<FusionEdgeVO> edges, List<FusionRelation> relations) {
        for (FusionRelation relation : relations) {
            FusionNodeVO source = resolveNode(relation.getSourceType(), relation.getSourceId());
            FusionNodeVO target = resolveNode(relation.getTargetType(), relation.getTargetId());
            if (source == null || target == null) {
                continue;
            }
            putNode(nodes, source);
            putNode(nodes, target);
            edges.add(edge(
                    relation.getSourceType(),
                    relation.getSourceId(),
                    relation.getTargetType(),
                    relation.getTargetId(),
                    relation.getRelationType(),
                    relation.getWeight(),
                    relation.getDescription()
            ));
        }
    }

    private FusionGraphVO graph(Map<String, FusionNodeVO> nodes, List<FusionEdgeVO> edges, Long studentId) {
        if (studentId != null && !nodes.isEmpty()) {
            applyScores(nodes, studentId);
        }
        List<FusionNodeVO> nodeList = new ArrayList<>(nodes.values());
        List<FusionNodeVO> weakPoints = nodeList.stream()
                .filter(this::isWeak)
                .sorted(Comparator.comparing(node -> node.getScore() == null ? BigDecimal.valueOf(999) : node.getScore()))
                .limit(5)
                .toList();
        FusionGraphVO graph = new FusionGraphVO();
        graph.setNodes(nodeList);
        graph.setEdges(edges);
        graph.setWeakPoints(weakPoints);
        graph.setRecommendedPath(weakPoints.stream()
                .map(node -> "优先补强：" + node.getLabel())
                .toList());
        return graph;
    }

    private void applyScores(Map<String, FusionNodeVO> nodes, Long studentId) {
        List<StudentCapabilityScore> scores = scoreMapper.selectList(new LambdaQueryWrapper<StudentCapabilityScore>()
                .eq(StudentCapabilityScore::getStudentId, studentId)
                .isNull(StudentCapabilityScore::getDeletedAt));
        Map<String, StudentCapabilityScore> scoreMap = scores.stream()
                .collect(Collectors.toMap(
                        score -> key(score.getTargetType(), score.getTargetId()),
                        score -> score,
                        (left, right) -> right
                ));
        for (FusionNodeVO node : nodes.values()) {
            StudentCapabilityScore score = scoreMap.get(node.getNodeKey());
            if (score != null) {
                node.setScore(score.getScore());
                node.setMasteryStatus(score.getMasteryStatus());
            }
        }
    }

    private FusionNodeVO resolveNode(String type, Long id) {
        if ("job_role".equals(type)) {
            JobRole role = jobRoleMapper.selectById(id);
            return role == null || role.getDeletedAt() != null ? null : node(type, id, role.getRoleName(), role.getDescription());
        }
        if (FusionRelationService.TYPE_JOB_CAPABILITY.equals(type)) {
            JobCapability capability = capabilityMapper.selectById(id);
            return capability == null || capability.getDeletedAt() != null ? null : node(type, id, capability.getCapabilityName(), capability.getDescription());
        }
        if (FusionRelationService.TYPE_COURSE_KNOWLEDGE_POINT.equals(type)) {
            CourseKnowledgePoint point = knowledgePointMapper.selectById(id);
            return point == null || point.getDeletedAt() != null ? null : node(type, id, point.getName(), point.getDescription());
        }
        if (FusionRelationService.TYPE_COMPETITION_TASK.equals(type)) {
            CompetitionTask task = competitionTaskMapper.selectById(id);
            return task == null || task.getDeletedAt() != null ? null : node(type, id, task.getTaskTitle(), task.getTaskDescription());
        }
        if (FusionRelationService.TYPE_CERTIFICATE_ASSESSMENT_POINT.equals(type)) {
            CertificateAssessmentPoint point = assessmentPointMapper.selectById(id);
            return point == null || point.getDeletedAt() != null ? null : node(type, id, point.getPointName(), point.getDescription());
        }
        return null;
    }

    private FusionNodeVO node(String type, Long id, String label, String description) {
        FusionNodeVO node = new FusionNodeVO();
        node.setNodeType(type);
        node.setNodeId(id);
        node.setNodeKey(key(type, id));
        node.setLabel(label);
        node.setDescription(description);
        return node;
    }

    private FusionEdgeVO edge(String sourceType, Long sourceId, String targetType, Long targetId, String relationType, BigDecimal weight, String description) {
        FusionEdgeVO edge = new FusionEdgeVO();
        edge.setSourceType(sourceType);
        edge.setSourceId(sourceId);
        edge.setSourceKey(key(sourceType, sourceId));
        edge.setTargetType(targetType);
        edge.setTargetId(targetId);
        edge.setTargetKey(key(targetType, targetId));
        edge.setRelationType(relationType);
        edge.setWeight(weight);
        edge.setDescription(description);
        return edge;
    }

    private void putNode(Map<String, FusionNodeVO> nodes, FusionNodeVO node) {
        nodes.putIfAbsent(node.getNodeKey(), node);
    }

    private boolean isWeak(FusionNodeVO node) {
        if ("weak".equals(node.getMasteryStatus()) || "developing".equals(node.getMasteryStatus())) {
            return true;
        }
        return node.getScore() != null && node.getScore().compareTo(BigDecimal.valueOf(70)) < 0;
    }

    private String key(String type, Long id) {
        return type + ":" + id;
    }

    private FusionGraphVO emptyGraph() {
        FusionGraphVO graph = new FusionGraphVO();
        graph.setNodes(List.of());
        graph.setEdges(List.of());
        graph.setWeakPoints(List.of());
        graph.setRecommendedPath(List.of());
        return graph;
    }

    private JobRole getJobRole(Long jobRoleId) {
        JobRole role = jobRoleMapper.selectById(jobRoleId);
        if (role == null || role.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "岗位能力模型不存在");
        }
        return role;
    }

    private Long currentStudentId() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        Student student = studentMapper.selectOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getUserId, currentUser.getUserId())
                .isNull(Student::getDeletedAt)
                .last("LIMIT 1"));
        if (student == null) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "当前账号未绑定学生信息");
        }
        return student.getId();
    }

    private void checkAssignedScope(Long studentId) {
        studentContextService.checkCanViewStudent(studentId);
    }
}
