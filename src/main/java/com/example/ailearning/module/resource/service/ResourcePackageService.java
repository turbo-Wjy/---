package com.example.ailearning.module.resource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.ai.entity.AiAgent;
import com.example.ailearning.module.ai.entity.AiGeneratedResource;
import com.example.ailearning.module.ai.entity.AiGenerationTask;
import com.example.ailearning.module.ai.entity.AiGenerationTaskLog;
import com.example.ailearning.module.ai.mapper.AiAgentMapper;
import com.example.ailearning.module.ai.mapper.AiGeneratedResourceMapper;
import com.example.ailearning.module.ai.mapper.AiGenerationTaskLogMapper;
import com.example.ailearning.module.ai.mapper.AiGenerationTaskMapper;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.resource.dto.AiGenerationTaskRequest;
import com.example.ailearning.module.resource.dto.ResourcePackageReviewRequest;
import com.example.ailearning.module.resource.entity.ResourcePackage;
import com.example.ailearning.module.resource.entity.ResourcePackageItem;
import com.example.ailearning.module.resource.mapper.ResourcePackageItemMapper;
import com.example.ailearning.module.resource.mapper.ResourcePackageMapper;
import com.example.ailearning.module.resource.vo.AiGeneratedResourceVO;
import com.example.ailearning.module.resource.vo.AiGenerationTaskLogVO;
import com.example.ailearning.module.resource.vo.AiGenerationTaskVO;
import com.example.ailearning.module.resource.vo.ResourcePackageVO;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.service.StudentContextService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ResourcePackageService {
    private static final String RESOURCE_DESIGNER_AGENT_CODE = "resource_designer_agent";
    private static final List<String> DEFAULT_RESOURCE_TYPES = List.of("handout", "ppt", "quiz", "mindmap", "practice_case");

    private final StudentContextService studentContextService;
    private final AiAgentMapper aiAgentMapper;
    private final AiGenerationTaskMapper taskMapper;
    private final AiGeneratedResourceMapper resourceMapper;
    private final AiGenerationTaskLogMapper taskLogMapper;
    private final ResourcePackageMapper packageMapper;
    private final ResourcePackageItemMapper packageItemMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public ResourcePackageService(
            StudentContextService studentContextService,
            AiAgentMapper aiAgentMapper,
            AiGenerationTaskMapper taskMapper,
            AiGeneratedResourceMapper resourceMapper,
            AiGenerationTaskLogMapper taskLogMapper,
            ResourcePackageMapper packageMapper,
            ResourcePackageItemMapper packageItemMapper,
            StudentProfileMapper studentProfileMapper,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.studentContextService = studentContextService;
        this.aiAgentMapper = aiAgentMapper;
        this.taskMapper = taskMapper;
        this.resourceMapper = resourceMapper;
        this.taskLogMapper = taskLogMapper;
        this.packageMapper = packageMapper;
        this.packageItemMapper = packageItemMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public AiGenerationTaskVO createGenerationTask(AiGenerationTaskRequest request) {
        Student student = studentContextService.currentStudentRequired();
        Long userId = CurrentUserHolder.getRequired().getUserId();
        AiAgent agent = resourceDesignerAgent();
        List<String> resourceTypes = normalizedResourceTypes(request.getResourceTypes());
        StudentProfile profile = latestProfile(student.getId());

        AiGenerationTask task = new AiGenerationTask();
        task.setStudentId(student.getId());
        task.setAgentId(agent.getId());
        task.setTaskType("resource_package_generation");
        task.setPrompt(prompt(request, resourceTypes));
        task.setContextSnapshotJson(toJson(Map.of(
                "targetJobRoleId", nullable(request.getTargetJobRoleId()),
                "courseId", nullable(request.getCourseId()),
                "competitionId", nullable(request.getCompetitionId()),
                "certificateId", nullable(request.getCertificateId()),
                "knowledgePointIds", nullable(request.getKnowledgePointIds()),
                "weakPointIds", nullable(request.getWeakPointIds()),
                "resourceTypes", resourceTypes,
                "difficulty", nullable(request.getDifficulty()),
                "scenario", nullable(request.getScenario()),
                "profileId", profile == null ? "" : profile.getId()
        )));
        task.setTaskStatus("running");
        task.setStartedAt(LocalDateTime.now());
        task.setStatus("active");
        task.setCreatedBy(userId);
        taskMapper.insert(task);
        log(task, "info", "资源设计智能体已接收资源包生成任务", Map.of("resourceTypes", resourceTypes));

        ResourcePackage resourcePackage = new ResourcePackage();
        resourcePackage.setStudentId(student.getId());
        resourcePackage.setTaskId(task.getId());
        resourcePackage.setProfileId(profile == null ? null : profile.getId());
        resourcePackage.setTargetJobRoleId(request.getTargetJobRoleId());
        resourcePackage.setCourseId(request.getCourseId());
        resourcePackage.setCompetitionId(request.getCompetitionId());
        resourcePackage.setCertificateId(request.getCertificateId());
        resourcePackage.setPackageTitle(packageTitle(request));
        resourcePackage.setGenerationContextJson(task.getContextSnapshotJson());
        resourcePackage.setResourceTypesJson(toJson(resourceTypes));
        resourcePackage.setDifficulty(request.getDifficulty());
        resourcePackage.setScenario(request.getScenario());
        resourcePackage.setReviewStatus("generated");
        resourcePackage.setStatus("generated");
        resourcePackage.setCreatedBy(userId);
        packageMapper.insert(resourcePackage);

        int order = 1;
        for (String type : resourceTypes) {
            AiGeneratedResource resource = new AiGeneratedResource();
            resource.setTaskId(task.getId());
            resource.setResourceType(type);
            resource.setTitle(resourceTitle(type));
            resource.setContentText(resourceContent(type, request));
            resource.setMetadataJson(toJson(Map.of(
                    "generatedBy", agent.getCode(),
                    "packageId", resourcePackage.getId(),
                    "difficulty", nullable(request.getDifficulty()),
                    "scenario", nullable(request.getScenario())
            )));
            resource.setStatus("active");
            resource.setCreatedBy(userId);
            resourceMapper.insert(resource);

            ResourcePackageItem item = new ResourcePackageItem();
            item.setPackageId(resourcePackage.getId());
            item.setResourceId(resource.getId());
            item.setItemOrder(order++);
            item.setStatus("active");
            item.setCreatedBy(userId);
            packageItemMapper.insert(item);
        }

        task.setTaskStatus("completed");
        task.setFinishedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        log(task, "info", "资源包生成完成", Map.of("packageId", resourcePackage.getId(), "resourceCount", resourceTypes.size()));
        auditService.operation("ai_learning_center", "generate_resource_package", "resource_package", resourcePackage.getId(), "success", "生成资源包，未记录敏感明文");
        return toTaskVO(task);
    }

    public AiGenerationTaskVO getTask(Long id) {
        AiGenerationTask task = getTaskEntity(id);
        checkTaskReadable(task);
        return toTaskVO(task);
    }

    @Transactional(rollbackFor = Exception.class)
    public AiGenerationTaskVO cancelTask(Long id) {
        AiGenerationTask task = getTaskEntity(id);
        checkTaskOwner(task);
        if ("completed".equals(task.getTaskStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "已完成任务不能取消");
        }
        task.setTaskStatus("cancelled");
        task.setFinishedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        log(task, "info", "学生取消资源包生成任务", Map.of("taskId", task.getId()));
        return toTaskVO(task);
    }

    @Transactional(rollbackFor = Exception.class)
    public AiGenerationTaskVO retryTask(Long id) {
        AiGenerationTask task = getTaskEntity(id);
        checkTaskOwner(task);
        task.setTaskStatus("queued");
        task.setErrorMessage(null);
        task.setStartedAt(null);
        task.setFinishedAt(null);
        taskMapper.updateById(task);
        log(task, "info", "学生请求重试资源包生成任务", Map.of("taskId", task.getId()));
        return toTaskVO(task);
    }

    public List<AiGenerationTaskLogVO> taskLogs(Long taskId) {
        AiGenerationTask task = getTaskEntity(taskId);
        checkTaskReadable(task);
        return taskLogMapper.selectList(new LambdaQueryWrapper<AiGenerationTaskLog>()
                        .eq(AiGenerationTaskLog::getTaskId, taskId)
                        .isNull(AiGenerationTaskLog::getDeletedAt)
                        .orderByAsc(AiGenerationTaskLog::getCreatedAt)
                        .orderByAsc(AiGenerationTaskLog::getId))
                .stream().map(this::toLogVO).toList();
    }

    public PageResult<ResourcePackageVO> pagePackages(PageQuery query, Long studentId, String reviewStatus) {
        Page<ResourcePackage> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<ResourcePackage> wrapper = new LambdaQueryWrapper<ResourcePackage>()
                .isNull(ResourcePackage::getDeletedAt)
                .orderByDesc(ResourcePackage::getCreatedAt);
        Student currentStudent = studentContextService.currentStudent();
        if (currentStudent != null) {
            wrapper.eq(ResourcePackage::getStudentId, currentStudent.getId());
        } else if (studentId != null) {
            studentContextService.checkCanViewStudent(studentId);
            wrapper.eq(ResourcePackage::getStudentId, studentId);
        }
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(ResourcePackage::getReviewStatus, reviewStatus);
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(ResourcePackage::getStatus, query.getStatus());
        }
        Page<ResourcePackage> result = packageMapper.selectPage(page, wrapper);
        List<ResourcePackageVO> items = result.getRecords().stream().map(pkg -> toPackageVO(pkg, false)).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public ResourcePackageVO getPackage(Long id) {
        ResourcePackage resourcePackage = getPackageEntity(id);
        checkPackageReadable(resourcePackage);
        return toPackageVO(resourcePackage, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourcePackageVO submitReview(Long id) {
        ResourcePackage resourcePackage = getPackageEntity(id);
        checkPackageOwner(resourcePackage);
        if (!"generated".equals(resourcePackage.getReviewStatus()) && !"rejected".equals(resourcePackage.getReviewStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "当前资源包状态不能提交审核");
        }
        resourcePackage.setReviewStatus("pending_review");
        resourcePackage.setStatus("pending_review");
        packageMapper.updateById(resourcePackage);
        auditService.operation("ai_learning_center", "submit_resource_package_review", "resource_package", id, "success", "学生提交资源包审核");
        return toPackageVO(resourcePackage, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourcePackageVO review(Long id, ResourcePackageReviewRequest request) {
        ResourcePackage resourcePackage = getPackageEntity(id);
        studentContextService.checkCanViewStudent(resourcePackage.getStudentId());
        if (!"approved".equals(request.getResult()) && !"rejected".equals(request.getResult())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "审核结果只能是 approved 或 rejected");
        }
        resourcePackage.setReviewStatus(request.getResult());
        resourcePackage.setStatus(request.getResult());
        packageMapper.updateById(resourcePackage);
        auditService.review("resource_package", id, "teacher_review", request.getResult(), request.getComment());
        auditService.operation("ai_learning_center", "review_resource_package", "resource_package", id, "success", "教师审核资源包");
        return toPackageVO(resourcePackage, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourcePackageVO publish(Long id) {
        ResourcePackage resourcePackage = getPackageEntity(id);
        studentContextService.checkCanViewStudent(resourcePackage.getStudentId());
        if (!"approved".equals(resourcePackage.getReviewStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "资源包审核通过后才能发布");
        }
        resourcePackage.setStatus("published");
        resourcePackage.setPublishedAt(LocalDateTime.now());
        packageMapper.updateById(resourcePackage);
        auditService.operation("ai_learning_center", "publish_resource_package", "resource_package", id, "success", "教师发布资源包");
        return toPackageVO(resourcePackage, true);
    }

    public AiGeneratedResourceVO getResource(Long id) {
        AiGeneratedResource resource = getResourceEntity(id);
        checkResourceReadable(resource);
        return toResourceVO(resource);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(Long id) {
        AiGeneratedResource resource = getResourceEntity(id);
        checkResourceOwner(resource);
        resource.setDeletedAt(DeleteConstants.now());
        resource.setStatus("deleted");
        resourceMapper.updateById(resource);
        auditService.operation("ai_learning_center", "delete_generated_resource", "ai_generated_resource", id, "success", "删除个人生成资源");
    }

    private AiAgent resourceDesignerAgent() {
        AiAgent agent = aiAgentMapper.selectOne(new LambdaQueryWrapper<AiAgent>()
                .eq(AiAgent::getCode, RESOURCE_DESIGNER_AGENT_CODE)
                .isNull(AiAgent::getDeletedAt)
                .last("LIMIT 1"));
        if (agent == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "资源设计智能体不存在，请先执行 seed.sql");
        }
        return agent;
    }

    private StudentProfile latestProfile(Long studentId) {
        return studentProfileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, studentId)
                .isNull(StudentProfile::getDeletedAt)
                .orderByDesc(StudentProfile::getProfileVersion)
                .last("LIMIT 1"));
    }

    private AiGenerationTask getTaskEntity(Long id) {
        AiGenerationTask task = taskMapper.selectById(id);
        if (task == null || task.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "AI生成任务不存在");
        }
        return task;
    }

    private ResourcePackage getPackageEntity(Long id) {
        ResourcePackage resourcePackage = packageMapper.selectById(id);
        if (resourcePackage == null || resourcePackage.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "资源包不存在");
        }
        return resourcePackage;
    }

    private AiGeneratedResource getResourceEntity(Long id) {
        AiGeneratedResource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "AI生成资源不存在");
        }
        return resource;
    }

    private void checkTaskOwner(AiGenerationTask task) {
        Long studentId = studentContextService.currentStudentIdRequired();
        if (!studentId.equals(task.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能操作自己的生成任务");
        }
    }

    private void checkTaskReadable(AiGenerationTask task) {
        Student currentStudent = studentContextService.currentStudent();
        if (currentStudent != null && !currentStudent.getId().equals(task.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能查看自己的生成任务");
        }
        if (currentStudent == null) {
            studentContextService.checkCanViewStudent(task.getStudentId());
        }
    }

    private void checkPackageOwner(ResourcePackage resourcePackage) {
        Long studentId = studentContextService.currentStudentIdRequired();
        if (!studentId.equals(resourcePackage.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能操作自己的资源包");
        }
    }

    private void checkPackageReadable(ResourcePackage resourcePackage) {
        Student currentStudent = studentContextService.currentStudent();
        if (currentStudent != null && !currentStudent.getId().equals(resourcePackage.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能查看自己的资源包");
        }
        if (currentStudent == null) {
            studentContextService.checkCanViewStudent(resourcePackage.getStudentId());
        }
    }

    private void checkResourceReadable(AiGeneratedResource resource) {
        ResourcePackage resourcePackage = packageByTask(resource.getTaskId());
        checkPackageReadable(resourcePackage);
    }

    private void checkResourceOwner(AiGeneratedResource resource) {
        ResourcePackage resourcePackage = packageByTask(resource.getTaskId());
        checkPackageOwner(resourcePackage);
    }

    private ResourcePackage packageByTask(Long taskId) {
        ResourcePackage resourcePackage = packageMapper.selectOne(new LambdaQueryWrapper<ResourcePackage>()
                .eq(ResourcePackage::getTaskId, taskId)
                .isNull(ResourcePackage::getDeletedAt)
                .last("LIMIT 1"));
        if (resourcePackage == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "资源包不存在");
        }
        return resourcePackage;
    }

    private AiGenerationTaskVO toTaskVO(AiGenerationTask task) {
        AiGenerationTaskVO vo = new AiGenerationTaskVO();
        vo.setId(task.getId());
        vo.setStudentId(task.getStudentId());
        vo.setAgentId(task.getAgentId());
        vo.setTaskType(task.getTaskType());
        vo.setTaskStatus(task.getTaskStatus());
        vo.setPrompt(task.getPrompt());
        vo.setContextSnapshot(task.getContextSnapshotJson());
        vo.setErrorMessage(task.getErrorMessage());
        vo.setStartedAt(task.getStartedAt());
        vo.setFinishedAt(task.getFinishedAt());
        ResourcePackage resourcePackage = packageMapper.selectOne(new LambdaQueryWrapper<ResourcePackage>()
                .eq(ResourcePackage::getTaskId, task.getId())
                .isNull(ResourcePackage::getDeletedAt)
                .last("LIMIT 1"));
        if (resourcePackage != null) {
            vo.setPackageId(resourcePackage.getId());
        }
        return vo;
    }

    private ResourcePackageVO toPackageVO(ResourcePackage resourcePackage, boolean includeResources) {
        ResourcePackageVO vo = new ResourcePackageVO();
        vo.setId(resourcePackage.getId());
        vo.setStudentId(resourcePackage.getStudentId());
        vo.setTaskId(resourcePackage.getTaskId());
        vo.setProfileId(resourcePackage.getProfileId());
        vo.setTargetJobRoleId(resourcePackage.getTargetJobRoleId());
        vo.setCourseId(resourcePackage.getCourseId());
        vo.setCompetitionId(resourcePackage.getCompetitionId());
        vo.setCertificateId(resourcePackage.getCertificateId());
        vo.setPackageTitle(resourcePackage.getPackageTitle());
        vo.setGenerationContext(resourcePackage.getGenerationContextJson());
        vo.setResourceTypes(resourcePackage.getResourceTypesJson());
        vo.setDifficulty(resourcePackage.getDifficulty());
        vo.setScenario(resourcePackage.getScenario());
        vo.setReviewStatus(resourcePackage.getReviewStatus());
        vo.setStatus(resourcePackage.getStatus());
        vo.setPublishedAt(resourcePackage.getPublishedAt());
        if (includeResources) {
            List<Long> resourceIds = packageItemMapper.selectList(new LambdaQueryWrapper<ResourcePackageItem>()
                            .eq(ResourcePackageItem::getPackageId, resourcePackage.getId())
                            .isNull(ResourcePackageItem::getDeletedAt)
                            .orderByAsc(ResourcePackageItem::getItemOrder))
                    .stream().map(ResourcePackageItem::getResourceId).toList();
            vo.setResources(resourceIds.isEmpty()
                    ? List.of()
                    : resourceMapper.selectList(new LambdaQueryWrapper<AiGeneratedResource>()
                    .in(AiGeneratedResource::getId, resourceIds)
                    .isNull(AiGeneratedResource::getDeletedAt))
                    .stream().map(this::toResourceVO).toList());
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

    private AiGenerationTaskLogVO toLogVO(AiGenerationTaskLog log) {
        AiGenerationTaskLogVO vo = new AiGenerationTaskLogVO();
        vo.setId(log.getId());
        vo.setTaskId(log.getTaskId());
        vo.setAgentId(log.getAgentId());
        vo.setLogLevel(log.getLogLevel());
        vo.setLogMessage(log.getLogMessage());
        vo.setPayload(log.getPayloadJson());
        vo.setCreatedAt(log.getCreatedAt());
        return vo;
    }

    private void log(AiGenerationTask task, String level, String message, Object payload) {
        AiGenerationTaskLog log = new AiGenerationTaskLog();
        log.setTaskId(task.getId());
        log.setAgentId(task.getAgentId());
        log.setLogLevel(level);
        log.setLogMessage(message);
        log.setPayloadJson(toJson(payload));
        log.setStatus("active");
        log.setCreatedBy(task.getCreatedBy());
        taskLogMapper.insert(log);
    }

    private List<String> normalizedResourceTypes(List<String> requested) {
        if (requested == null || requested.isEmpty()) {
            return DEFAULT_RESOURCE_TYPES;
        }
        return requested.stream().filter(type -> type != null && !type.isBlank()).distinct().toList();
    }

    private String prompt(AiGenerationTaskRequest request, List<String> resourceTypes) {
        if (request.getPrompt() != null && !request.getPrompt().isBlank()) {
            return request.getPrompt();
        }
        return "基于学生画像、目标岗位、课程知识点和融合关系生成个性化资源包：" + String.join(",", resourceTypes);
    }

    private String packageTitle(AiGenerationTaskRequest request) {
        String scenario = request.getScenario() == null || request.getScenario().isBlank() ? "个性化学习" : request.getScenario();
        return "AI多智能体资源包-" + scenario;
    }

    private String resourceTitle(String type) {
        return switch (type) {
            case "handout" -> "个性化课程讲解文档";
            case "ppt" -> "微课PPT设计稿";
            case "quiz" -> "分层练习题库";
            case "mindmap" -> "知识点思维导图";
            case "code_case" -> "代码实操案例";
            case "practice_case" -> "实践案例任务";
            case "reading" -> "拓展阅读材料";
            case "video_script" -> "视频/动画脚本";
            default -> "个性化学习资源-" + type;
        };
    }

    private String resourceContent(String type, AiGenerationTaskRequest request) {
        return """
                # %s

                生成场景：%s
                难度层级：%s
                目标岗位模型：%s
                关联课程：%s

                这是第一版多智能体资源生成占位内容。后续接入真实大模型后，可在保持接口和数据库不变的前提下，将此内容替换为文档、PPT、题库、思维导图、视频脚本或实操案例的真实生成结果。
                """.formatted(resourceTitle(type), request.getScenario(), request.getDifficulty(), request.getTargetJobRoleId(), request.getCourseId());
    }

    private Object nullable(Object value) {
        return value == null ? "" : value;
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "JSON 字段格式不正确");
        }
    }
}
