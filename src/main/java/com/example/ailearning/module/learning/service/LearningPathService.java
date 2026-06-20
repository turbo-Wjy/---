package com.example.ailearning.module.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.ai.entity.AiAgent;
import com.example.ailearning.module.ai.entity.AiGeneratedResource;
import com.example.ailearning.module.ai.entity.AiGenerationTask;
import com.example.ailearning.module.ai.mapper.AiAgentMapper;
import com.example.ailearning.module.ai.mapper.AiGeneratedResourceMapper;
import com.example.ailearning.module.ai.mapper.AiGenerationTaskMapper;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.learning.dto.LearningPathAdjustRequest;
import com.example.ailearning.module.learning.dto.LearningPathGenerateRequest;
import com.example.ailearning.module.learning.entity.LearningPath;
import com.example.ailearning.module.learning.entity.LearningPathStep;
import com.example.ailearning.module.learning.entity.ResourceRecommendation;
import com.example.ailearning.module.learning.mapper.LearningPathMapper;
import com.example.ailearning.module.learning.mapper.LearningPathStepMapper;
import com.example.ailearning.module.learning.mapper.ResourceRecommendationMapper;
import com.example.ailearning.module.learning.vo.LearningPathStepVO;
import com.example.ailearning.module.learning.vo.LearningPathVO;
import com.example.ailearning.module.learning.vo.ResourceRecommendationVO;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.resource.vo.AiGeneratedResourceVO;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.service.StudentContextService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LearningPathService {
    private static final String LEARNING_PATH_AGENT_CODE = "learning_path_agent";

    private final StudentContextService studentContextService;
    private final AiAgentMapper aiAgentMapper;
    private final AiGenerationTaskMapper taskMapper;
    private final AiGeneratedResourceMapper resourceMapper;
    private final StudentProfileMapper profileMapper;
    private final LearningPathMapper pathMapper;
    private final LearningPathStepMapper stepMapper;
    private final ResourceRecommendationMapper recommendationMapper;
    private final AuditService auditService;

    public LearningPathService(
            StudentContextService studentContextService,
            AiAgentMapper aiAgentMapper,
            AiGenerationTaskMapper taskMapper,
            AiGeneratedResourceMapper resourceMapper,
            StudentProfileMapper profileMapper,
            LearningPathMapper pathMapper,
            LearningPathStepMapper stepMapper,
            ResourceRecommendationMapper recommendationMapper,
            AuditService auditService
    ) {
        this.studentContextService = studentContextService;
        this.aiAgentMapper = aiAgentMapper;
        this.taskMapper = taskMapper;
        this.resourceMapper = resourceMapper;
        this.profileMapper = profileMapper;
        this.pathMapper = pathMapper;
        this.stepMapper = stepMapper;
        this.recommendationMapper = recommendationMapper;
        this.auditService = auditService;
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningPathVO generate(LearningPathGenerateRequest request) {
        Student student = studentContextService.currentStudentRequired();
        Long userId = CurrentUserHolder.getRequired().getUserId();
        AiAgent agent = learningPathAgent();
        StudentProfile profile = latestProfile(student.getId());

        LearningPath path = new LearningPath();
        path.setStudentId(student.getId());
        path.setTitle(pathTitle(request));
        path.setGoal(pathGoal(request));
        path.setGeneratedByAgentId(agent.getId());
        path.setPathStatus("generated");
        path.setStatus("active");
        path.setCreatedBy(userId);
        pathMapper.insert(path);

        List<AiGeneratedResource> resources = candidateResources(student.getId(), request.getPreferredResourceTypes());
        List<LearningPathStep> steps = buildSteps(path.getId(), resources, request, userId);
        for (LearningPathStep step : steps) {
            stepMapper.insert(step);
        }
        for (AiGeneratedResource resource : resources) {
            upsertRecommendation(student.getId(), resource.getId(), profile == null ? null : profile.getId(), recommendReason(request, resource), userId);
        }
        auditService.operation("ai_learning_center", "generate_learning_path", "learning_path", path.getId(), "success", "基于画像、融合图谱和资源包生成学习路径");
        return toPathVO(path, true);
    }

    public List<LearningPathVO> myPaths() {
        Long studentId = studentContextService.currentStudentIdRequired();
        return pathMapper.selectList(new LambdaQueryWrapper<LearningPath>()
                        .eq(LearningPath::getStudentId, studentId)
                        .isNull(LearningPath::getDeletedAt)
                        .orderByDesc(LearningPath::getCreatedAt))
                .stream().map(path -> toPathVO(path, false)).toList();
    }

    public LearningPathVO get(Long id) {
        LearningPath path = getPathEntity(id);
        checkPathReadable(path);
        return toPathVO(path, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningPathVO accept(Long id) {
        LearningPath path = getPathEntity(id);
        checkPathOwner(path);
        path.setPathStatus("accepted");
        pathMapper.updateById(path);
        auditService.operation("ai_learning_center", "accept_learning_path", "learning_path", id, "success", "学生接受学习路径");
        return toPathVO(path, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningPathVO adjust(Long id, LearningPathAdjustRequest request) {
        LearningPath path = getPathEntity(id);
        checkPathOwner(path);
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            path.setTitle(request.getTitle());
        }
        if (request.getGoal() != null && !request.getGoal().isBlank()) {
            path.setGoal(request.getGoal());
        }
        path.setPathStatus("adjusted");
        pathMapper.updateById(path);
        auditService.operation("ai_learning_center", "adjust_learning_path", "learning_path", id, "success", "学生调整学习路径");
        return toPathVO(path, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningPathStepVO completeStep(Long stepId) {
        LearningPathStep step = getStepEntity(stepId);
        LearningPath path = getPathEntity(step.getPathId());
        checkPathOwner(path);
        step.setCompletionStatus("completed");
        stepMapper.updateById(step);
        auditService.operation("ai_learning_center", "complete_learning_path_step", "learning_path_step", stepId, "success", "学生完成学习路径步骤");
        return toStepVO(step, true);
    }

    public List<AiGeneratedResourceVO> pathResources(Long pathId) {
        LearningPath path = getPathEntity(pathId);
        checkPathReadable(path);
        return steps(pathId).stream()
                .map(LearningPathStep::getResourceId)
                .filter(id -> id != null)
                .distinct()
                .map(resourceMapper::selectById)
                .filter(resource -> resource != null && resource.getDeletedAt() == null)
                .map(this::toResourceVO)
                .toList();
    }

    public List<ResourceRecommendationVO> myRecommendations(String viewStatus) {
        Long studentId = studentContextService.currentStudentIdRequired();
        LambdaQueryWrapper<ResourceRecommendation> wrapper = new LambdaQueryWrapper<ResourceRecommendation>()
                .eq(ResourceRecommendation::getStudentId, studentId)
                .isNull(ResourceRecommendation::getDeletedAt)
                .orderByDesc(ResourceRecommendation::getCreatedAt);
        if (viewStatus != null && !viewStatus.isBlank()) {
            wrapper.eq(ResourceRecommendation::getViewStatus, viewStatus);
        }
        return recommendationMapper.selectList(wrapper).stream().map(this::toRecommendationVO).toList();
    }

    private List<LearningPathStep> buildSteps(Long pathId, List<AiGeneratedResource> resources, LearningPathGenerateRequest request, Long userId) {
        List<LearningPathStep> steps = new ArrayList<>();
        int order = 1;
        for (AiGeneratedResource resource : resources) {
            LearningPathStep step = new LearningPathStep();
            step.setPathId(pathId);
            step.setStepOrder(order++);
            step.setTitle("学习资源：" + resource.getTitle());
            step.setResourceId(resource.getId());
            step.setExpectedDuration(expectedDuration(resource.getResourceType()));
            step.setCompletionStatus("not_started");
            step.setStatus("active");
            step.setCreatedBy(userId);
            steps.add(step);
        }
        if (steps.isEmpty()) {
            List<String> fallback = List.of(
                    "复盘学习画像并明确目标岗位能力差距",
                    "学习目标岗位关联课程知识点",
                    "完成对应题库练习和错题整理",
                    "结合竞赛或证书要求完成实操任务"
            );
            for (String title : fallback) {
                LearningPathStep step = new LearningPathStep();
                step.setPathId(pathId);
                step.setStepOrder(order++);
                step.setTitle(title);
                step.setExpectedDuration(45);
                step.setCompletionStatus("not_started");
                step.setStatus("active");
                step.setCreatedBy(userId);
                steps.add(step);
            }
        }
        return steps;
    }

    private List<AiGeneratedResource> candidateResources(Long studentId, List<String> preferredTypes) {
        List<AiGeneratedResource> resources = resourceMapper.selectList(new LambdaQueryWrapper<AiGeneratedResource>()
                .eq(AiGeneratedResource::getStatus, "active")
                .isNull(AiGeneratedResource::getDeletedAt)
                .orderByDesc(AiGeneratedResource::getCreatedAt));
        Set<String> preferred = preferredTypes == null ? Set.of() : preferredTypes.stream().collect(Collectors.toSet());
        return resources.stream()
                .filter(resource -> belongsToStudent(resource, studentId))
                .sorted(Comparator.comparing((AiGeneratedResource r) -> preferred.isEmpty() || preferred.contains(r.getResourceType()) ? 0 : 1)
                        .thenComparing(AiGeneratedResource::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(8)
                .toList();
    }

    private boolean belongsToStudent(AiGeneratedResource resource, Long studentId) {
        if (resource.getTaskId() == null) {
            return false;
        }
        AiGenerationTask task = taskMapper.selectById(resource.getTaskId());
        return task != null && task.getDeletedAt() == null && studentId.equals(task.getStudentId());
    }

    private void upsertRecommendation(Long studentId, Long resourceId, Long profileId, String reason, Long userId) {
        boolean exists = recommendationMapper.exists(new LambdaQueryWrapper<ResourceRecommendation>()
                .eq(ResourceRecommendation::getStudentId, studentId)
                .eq(ResourceRecommendation::getResourceId, resourceId)
                .isNull(ResourceRecommendation::getDeletedAt));
        if (exists) {
            return;
        }
        ResourceRecommendation recommendation = new ResourceRecommendation();
        recommendation.setStudentId(studentId);
        recommendation.setResourceId(resourceId);
        recommendation.setRecommendReason(reason);
        recommendation.setSourceProfileId(profileId);
        recommendation.setViewStatus("unread");
        recommendation.setStatus("active");
        recommendation.setCreatedBy(userId);
        recommendationMapper.insert(recommendation);
    }

    private LearningPath getPathEntity(Long id) {
        LearningPath path = pathMapper.selectById(id);
        if (path == null || path.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习路径不存在");
        }
        return path;
    }

    private LearningPathStep getStepEntity(Long id) {
        LearningPathStep step = stepMapper.selectById(id);
        if (step == null || step.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习路径步骤不存在");
        }
        return step;
    }

    private void checkPathOwner(LearningPath path) {
        Long studentId = studentContextService.currentStudentIdRequired();
        if (!studentId.equals(path.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能操作自己的学习路径");
        }
    }

    private void checkPathReadable(LearningPath path) {
        Student currentStudent = studentContextService.currentStudent();
        if (currentStudent != null && !currentStudent.getId().equals(path.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能查看自己的学习路径");
        }
        if (currentStudent == null) {
            studentContextService.checkCanViewStudent(path.getStudentId());
        }
    }

    private List<LearningPathStep> steps(Long pathId) {
        return stepMapper.selectList(new LambdaQueryWrapper<LearningPathStep>()
                .eq(LearningPathStep::getPathId, pathId)
                .isNull(LearningPathStep::getDeletedAt)
                .orderByAsc(LearningPathStep::getStepOrder)
                .orderByAsc(LearningPathStep::getId));
    }

    private LearningPathVO toPathVO(LearningPath path, boolean includeSteps) {
        LearningPathVO vo = new LearningPathVO();
        vo.setId(path.getId());
        vo.setStudentId(path.getStudentId());
        vo.setTitle(path.getTitle());
        vo.setGoal(path.getGoal());
        vo.setGeneratedByAgentId(path.getGeneratedByAgentId());
        vo.setPathStatus(path.getPathStatus());
        vo.setStatus(path.getStatus());
        vo.setCreatedAt(path.getCreatedAt());
        if (includeSteps) {
            vo.setSteps(steps(path.getId()).stream().map(step -> toStepVO(step, true)).toList());
        }
        return vo;
    }

    private LearningPathStepVO toStepVO(LearningPathStep step, boolean includeResource) {
        LearningPathStepVO vo = new LearningPathStepVO();
        vo.setId(step.getId());
        vo.setPathId(step.getPathId());
        vo.setStepOrder(step.getStepOrder());
        vo.setTitle(step.getTitle());
        vo.setResourceId(step.getResourceId());
        vo.setExpectedDuration(step.getExpectedDuration());
        vo.setCompletionStatus(step.getCompletionStatus());
        vo.setStatus(step.getStatus());
        if (includeResource && step.getResourceId() != null) {
            AiGeneratedResource resource = resourceMapper.selectById(step.getResourceId());
            if (resource != null && resource.getDeletedAt() == null) {
                vo.setResource(toResourceVO(resource));
            }
        }
        return vo;
    }

    private ResourceRecommendationVO toRecommendationVO(ResourceRecommendation recommendation) {
        ResourceRecommendationVO vo = new ResourceRecommendationVO();
        vo.setId(recommendation.getId());
        vo.setStudentId(recommendation.getStudentId());
        vo.setResourceId(recommendation.getResourceId());
        vo.setRecommendReason(recommendation.getRecommendReason());
        vo.setSourceProfileId(recommendation.getSourceProfileId());
        vo.setViewStatus(recommendation.getViewStatus());
        vo.setStatus(recommendation.getStatus());
        vo.setCreatedAt(recommendation.getCreatedAt());
        AiGeneratedResource resource = resourceMapper.selectById(recommendation.getResourceId());
        if (resource != null && resource.getDeletedAt() == null) {
            vo.setResource(toResourceVO(resource));
        }
        return vo;
    }

    private AiGeneratedResourceVO toResourceVO(AiGeneratedResource resource) {
        AiGeneratedResourceVO vo = new AiGeneratedResourceVO();
        vo.setId(resource.getId());
        vo.setTaskId(resource.getTaskId());
        vo.setResourceType(resource.getResourceType());
        vo.setTitle(resource.getTitle());
        vo.setContentUrl(resource.getContentUrl());
        vo.setContentText(resource.getContentText());
        vo.setMetadata(resource.getMetadataJson());
        vo.setStatus(resource.getStatus());
        return vo;
    }

    private AiAgent learningPathAgent() {
        AiAgent agent = aiAgentMapper.selectOne(new LambdaQueryWrapper<AiAgent>()
                .eq(AiAgent::getCode, LEARNING_PATH_AGENT_CODE)
                .isNull(AiAgent::getDeletedAt)
                .last("LIMIT 1"));
        if (agent == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "学习路径规划智能体不存在，请先执行 seed.sql");
        }
        return agent;
    }

    private StudentProfile latestProfile(Long studentId) {
        return profileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, studentId)
                .isNull(StudentProfile::getDeletedAt)
                .orderByDesc(StudentProfile::getProfileVersion)
                .last("LIMIT 1"));
    }

    private String pathTitle(LearningPathGenerateRequest request) {
        int weeks = request.getDurationWeeks() == null ? 4 : request.getDurationWeeks();
        return weeks + "周个性化学习路径";
    }

    private String pathGoal(LearningPathGenerateRequest request) {
        if (request.getLearningGoal() != null && !request.getLearningGoal().isBlank()) {
            return request.getLearningGoal();
        }
        return "基于学习画像、目标岗位、融合图谱和资源包推荐，完成阶段性能力提升。";
    }

    private String recommendReason(LearningPathGenerateRequest request, AiGeneratedResource resource) {
        return "根据你的学习画像、目标岗位和偏好资源类型，推荐学习：" + resource.getTitle();
    }

    private Integer expectedDuration(String resourceType) {
        return switch (resourceType == null ? "" : resourceType) {
            case "ppt", "mindmap" -> 30;
            case "quiz" -> 40;
            case "code_case", "practice_case" -> 60;
            case "video_script" -> 35;
            default -> 45;
        };
    }
}
