package com.example.ailearning.module.employment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.crypto.AesGcmCryptoService;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.ai.entity.AiAgent;
import com.example.ailearning.module.ai.entity.AiGenerationTask;
import com.example.ailearning.module.ai.mapper.AiAgentMapper;
import com.example.ailearning.module.ai.mapper.AiGenerationTaskMapper;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.certificate.entity.CertificateResult;
import com.example.ailearning.module.certificate.mapper.CertificateResultMapper;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.mapper.CompetitionResultMapper;
import com.example.ailearning.module.course.entity.LearningRecord;
import com.example.ailearning.module.course.mapper.LearningRecordMapper;
import com.example.ailearning.module.employment.dto.ResumeConfirmRequest;
import com.example.ailearning.module.employment.dto.ResumeGenerateRequest;
import com.example.ailearning.module.employment.entity.JobPost;
import com.example.ailearning.module.employment.entity.Resume;
import com.example.ailearning.module.employment.mapper.ResumeMapper;
import com.example.ailearning.module.employment.vo.ResumeVO;
import com.example.ailearning.module.evaluation.entity.LearningEvaluation;
import com.example.ailearning.module.evaluation.mapper.LearningEvaluationMapper;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.service.StudentContextService;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResumeService {
    private static final String RESUME_AGENT_CODE = "resume_generator_agent";

    private final StudentContextService studentContextService;
    private final JobPostService jobPostService;
    private final ResumeMapper resumeMapper;
    private final UserMapper userMapper;
    private final StudentProfileMapper profileMapper;
    private final LearningRecordMapper learningRecordMapper;
    private final CompetitionResultMapper competitionResultMapper;
    private final CertificateResultMapper certificateResultMapper;
    private final LearningEvaluationMapper evaluationMapper;
    private final AiAgentMapper aiAgentMapper;
    private final AiGenerationTaskMapper taskMapper;
    private final AesGcmCryptoService cryptoService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public ResumeService(
            StudentContextService studentContextService,
            JobPostService jobPostService,
            ResumeMapper resumeMapper,
            UserMapper userMapper,
            StudentProfileMapper profileMapper,
            LearningRecordMapper learningRecordMapper,
            CompetitionResultMapper competitionResultMapper,
            CertificateResultMapper certificateResultMapper,
            LearningEvaluationMapper evaluationMapper,
            AiAgentMapper aiAgentMapper,
            AiGenerationTaskMapper taskMapper,
            AesGcmCryptoService cryptoService,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.studentContextService = studentContextService;
        this.jobPostService = jobPostService;
        this.resumeMapper = resumeMapper;
        this.userMapper = userMapper;
        this.profileMapper = profileMapper;
        this.learningRecordMapper = learningRecordMapper;
        this.competitionResultMapper = competitionResultMapper;
        this.certificateResultMapper = certificateResultMapper;
        this.evaluationMapper = evaluationMapper;
        this.aiAgentMapper = aiAgentMapper;
        this.taskMapper = taskMapper;
        this.cryptoService = cryptoService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResumeVO generate(ResumeGenerateRequest request) {
        Student student = studentContextService.currentStudentRequired();
        JobPost jobPost = request.getTargetJobId() == null ? null : jobPostService.getEntity(request.getTargetJobId());
        User user = userMapper.selectById(student.getUserId());
        ResumeContext context = buildContext(student, jobPost);

        AiGenerationTask task = new AiGenerationTask();
        task.setStudentId(student.getId());
        task.setAgentId(resumeAgentId());
        task.setTaskType("resume");
        task.setPrompt("基于学生画像、课程学习、竞赛证书成果和目标岗位生成AI简历");
        task.setContextSnapshotJson(toJson(context.snapshot));
        task.setTaskStatus("succeeded");
        task.setStartedAt(LocalDateTime.now());
        task.setFinishedAt(LocalDateTime.now());
        task.setStatus("active");
        task.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        taskMapper.insert(task);

        String content = buildResumeContent(user, student, jobPost, context, request);
        AesGcmCryptoService.EncryptedValue encrypted = cryptoService.encrypt(content);
        Resume resume = new Resume();
        resume.setStudentId(student.getId());
        resume.setTargetJobId(request.getTargetJobId());
        resume.setGeneratedByTaskId(task.getId());
        resume.setResumeContentEncrypted(encrypted.cipherText());
        resume.setResumeContentIv(encrypted.iv());
        resume.setResumeSummary(buildSummary(user, jobPost, context));
        resume.setStudentConfirmed(false);
        resume.setStatus("draft");
        resume.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        resumeMapper.insert(resume);
        auditService.operation("job_ability", "generate_ai_resume", "resume", resume.getId(), "success", "学生生成AI简历草稿");
        return toVO(resume);
    }

    public List<ResumeVO> myResumes() {
        Long studentId = studentContextService.currentStudentIdRequired();
        return resumeMapper.selectList(new LambdaQueryWrapper<Resume>()
                        .eq(Resume::getStudentId, studentId)
                        .isNull(Resume::getDeletedAt)
                        .orderByDesc(Resume::getCreatedAt))
                .stream().map(this::toVO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResumeVO confirm(Long id, ResumeConfirmRequest request) {
        Resume resume = ownedResume(id);
        AesGcmCryptoService.EncryptedValue encrypted = cryptoService.encrypt(request.getResumeContent());
        resume.setResumeContentEncrypted(encrypted.cipherText());
        resume.setResumeContentIv(encrypted.iv());
        resume.setResumeSummary(request.getResumeSummary() == null || request.getResumeSummary().isBlank() ? summarizeText(request.getResumeContent()) : request.getResumeSummary());
        resume.setStudentConfirmed(true);
        resume.setConfirmedAt(LocalDateTime.now());
        resume.setStatus("confirmed");
        resumeMapper.updateById(resume);
        auditService.operation("job_ability", "confirm_resume", "resume", resume.getId(), "success", "学生确认AI简历");
        return toVO(resume);
    }

    Resume ownedResume(Long id) {
        Long studentId = studentContextService.currentStudentIdRequired();
        Resume resume = resumeMapper.selectById(id);
        if (resume == null || resume.getDeletedAt() != null || !studentId.equals(resume.getStudentId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "简历不存在");
        }
        return resume;
    }

    ResumeVO toVO(Resume resume) {
        ResumeVO vo = new ResumeVO();
        vo.setId(resume.getId());
        vo.setStudentId(resume.getStudentId());
        vo.setTargetJobId(resume.getTargetJobId());
        vo.setGeneratedByTaskId(resume.getGeneratedByTaskId());
        vo.setResumeContent(safeDecrypt(resume.getResumeContentEncrypted(), resume.getResumeContentIv()));
        vo.setResumeSummary(resume.getResumeSummary());
        vo.setStudentConfirmed(resume.getStudentConfirmed());
        vo.setConfirmedAt(resume.getConfirmedAt());
        vo.setStatus(resume.getStatus());
        vo.setCreatedAt(resume.getCreatedAt());
        return vo;
    }

    private ResumeContext buildContext(Student student, JobPost jobPost) {
        ResumeContext context = new ResumeContext();
        StudentProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, student.getId())
                .isNull(StudentProfile::getDeletedAt)
                .orderByDesc(StudentProfile::getProfileVersion)
                .last("LIMIT 1"));
        context.profileSummary = profile == null ? "" : safeDecrypt(profile.getProfileSummaryEncrypted(), profile.getProfileSummaryIv());
        context.learningRecordCount = learningRecordMapper.selectCount(new LambdaQueryWrapper<LearningRecord>().eq(LearningRecord::getStudentId, student.getId()).isNull(LearningRecord::getDeletedAt));
        context.competitionApprovedCount = competitionResultMapper.selectCount(new LambdaQueryWrapper<CompetitionResult>().eq(CompetitionResult::getStudentId, student.getId()).eq(CompetitionResult::getReviewStatus, "approved").isNull(CompetitionResult::getDeletedAt));
        context.certificateApprovedCount = certificateResultMapper.selectCount(new LambdaQueryWrapper<CertificateResult>().eq(CertificateResult::getStudentId, student.getId()).eq(CertificateResult::getReviewStatus, "approved").isNull(CertificateResult::getDeletedAt));
        LearningEvaluation evaluation = evaluationMapper.selectOne(new LambdaQueryWrapper<LearningEvaluation>()
                .eq(LearningEvaluation::getStudentId, student.getId())
                .isNull(LearningEvaluation::getDeletedAt)
                .orderByDesc(LearningEvaluation::getCreatedAt)
                .last("LIMIT 1"));
        context.evaluationSummary = evaluation == null ? "" : evaluation.getEvaluationSummary();
        context.snapshot.put("profileSummary", context.profileSummary);
        context.snapshot.put("learningRecordCount", context.learningRecordCount);
        context.snapshot.put("competitionApprovedCount", context.competitionApprovedCount);
        context.snapshot.put("certificateApprovedCount", context.certificateApprovedCount);
        context.snapshot.put("evaluationSummary", context.evaluationSummary);
        context.snapshot.put("targetJobTitle", jobPost == null ? "" : jobPost.getTitle());
        return context;
    }

    private String buildResumeContent(User user, Student student, JobPost jobPost, ResumeContext context, ResumeGenerateRequest request) {
        String target = jobPost == null ? valueOrDefault(request.getTargetPosition(), "目标岗位") : jobPost.getTitle();
        return """
                # AI简历初稿

                ## 基本信息
                姓名：%s
                学号：%s
                目标岗位：%s

                ## 个人画像摘要
                %s

                ## 学习与能力积累
                - 课程学习记录：%d 条
                - 最近学习评估：%s

                ## 岗课赛证成果
                - 已审核通过竞赛成果：%d 项
                - 已审核通过证书成果：%d 项

                ## 岗位匹配说明
                %s

                ## 自我陈述
                我希望将课程学习、竞赛训练、证书能力和项目实践持续转化为岗位能力，并在真实业务场景中提升解决问题的能力。
                """.formatted(
                user == null ? "" : user.getRealName(),
                student.getStudentNo(),
                target,
                valueOrDefault(context.profileSummary, "暂无画像摘要，建议先完善学习画像。"),
                context.learningRecordCount,
                valueOrDefault(context.evaluationSummary, "暂无学习评估，建议先生成学习效果评估。"),
                context.competitionApprovedCount,
                context.certificateApprovedCount,
                jobPost == null ? "当前简历未绑定具体企业岗位，可由学生继续补充岗位要求。" : valueOrDefault(jobPost.getRequirements(), "岗位要求待企业导师补充。")
        );
    }

    private String buildSummary(User user, JobPost jobPost, ResumeContext context) {
        return (user == null ? "学生" : user.getRealName()) + " 面向 " + (jobPost == null ? "目标岗位" : jobPost.getTitle())
                + " 的AI简历摘要：学习记录 " + context.learningRecordCount
                + " 条，竞赛成果 " + context.competitionApprovedCount
                + " 项，证书成果 " + context.certificateApprovedCount + " 项。";
    }

    private Long resumeAgentId() {
        AiAgent agent = aiAgentMapper.selectOne(new LambdaQueryWrapper<AiAgent>()
                .eq(AiAgent::getCode, RESUME_AGENT_CODE)
                .isNull(AiAgent::getDeletedAt)
                .last("LIMIT 1"));
        return agent == null ? null : agent.getId();
    }

    private String safeDecrypt(String encrypted, String iv) {
        try {
            return cryptoService.decrypt(encrypted, iv);
        } catch (BusinessException e) {
            return "";
        }
    }

    private String summarizeText(String value) {
        return value == null || value.length() <= 80 ? value : value.substring(0, 80) + "...";
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "JSON 字段格式不正确");
        }
    }

    private static class ResumeContext {
        private String profileSummary;
        private long learningRecordCount;
        private long competitionApprovedCount;
        private long certificateApprovedCount;
        private String evaluationSummary;
        private Map<String, Object> snapshot = new LinkedHashMap<>();
    }
}
