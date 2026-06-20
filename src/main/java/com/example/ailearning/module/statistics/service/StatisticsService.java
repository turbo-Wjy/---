package com.example.ailearning.module.statistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.course.entity.LearningRecord;
import com.example.ailearning.module.course.entity.QuizAttempt;
import com.example.ailearning.module.course.entity.WrongQuestion;
import com.example.ailearning.module.course.mapper.LearningRecordMapper;
import com.example.ailearning.module.course.mapper.QuizAttemptMapper;
import com.example.ailearning.module.course.mapper.WrongQuestionMapper;
import com.example.ailearning.module.dashboard.vo.DashboardMetricVO;
import com.example.ailearning.module.evaluation.entity.LearningEvaluation;
import com.example.ailearning.module.evaluation.mapper.LearningEvaluationMapper;
import com.example.ailearning.module.fusion.entity.FusionRelation;
import com.example.ailearning.module.fusion.entity.StudentCapabilityScore;
import com.example.ailearning.module.fusion.mapper.FusionRelationMapper;
import com.example.ailearning.module.fusion.mapper.StudentCapabilityScoreMapper;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.statistics.dto.ExportRequest;
import com.example.ailearning.module.statistics.entity.ExportRecord;
import com.example.ailearning.module.statistics.mapper.ExportRecordMapper;
import com.example.ailearning.module.statistics.vo.ExportRecordVO;
import com.example.ailearning.module.statistics.vo.StatisticsOverviewVO;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.mapper.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class StatisticsService {
    private final StudentMapper studentMapper;
    private final StudentProfileMapper profileMapper;
    private final StudentCapabilityScoreMapper capabilityScoreMapper;
    private final FusionRelationMapper fusionRelationMapper;
    private final LearningRecordMapper learningRecordMapper;
    private final QuizAttemptMapper quizAttemptMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final LearningEvaluationMapper evaluationMapper;
    private final ExportRecordMapper exportRecordMapper;
    private final AuditService auditService;

    public StatisticsService(
            StudentMapper studentMapper,
            StudentProfileMapper profileMapper,
            StudentCapabilityScoreMapper capabilityScoreMapper,
            FusionRelationMapper fusionRelationMapper,
            LearningRecordMapper learningRecordMapper,
            QuizAttemptMapper quizAttemptMapper,
            WrongQuestionMapper wrongQuestionMapper,
            LearningEvaluationMapper evaluationMapper,
            ExportRecordMapper exportRecordMapper,
            AuditService auditService
    ) {
        this.studentMapper = studentMapper;
        this.profileMapper = profileMapper;
        this.capabilityScoreMapper = capabilityScoreMapper;
        this.fusionRelationMapper = fusionRelationMapper;
        this.learningRecordMapper = learningRecordMapper;
        this.quizAttemptMapper = quizAttemptMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.evaluationMapper = evaluationMapper;
        this.exportRecordMapper = exportRecordMapper;
        this.auditService = auditService;
    }

    public StatisticsOverviewVO profileStatistics(Long majorId) {
        List<Long> studentIds = studentIdsByMajor(majorId);
        long studentCount = countStudents(majorId);
        long profiledCount = countProfiles(studentIds, majorId);
        BigDecimal rate = percent(profiledCount, studentCount);
        return overview("profile", majorId, List.of(
                metric("student_count", "学生数", studentCount, "人"),
                metric("profiled_student_count", "已建画像学生", profiledCount, "人"),
                metric("profile_completion_rate", "画像覆盖率", rate, "%")
        ));
    }

    public StatisticsOverviewVO fusionStatistics(Long majorId) {
        List<Long> studentIds = studentIdsByMajor(majorId);
        long relationCount = fusionRelationMapper.selectCount(new LambdaQueryWrapper<FusionRelation>().isNull(FusionRelation::getDeletedAt));
        long scoreCount = countCapabilityScores(studentIds, majorId, false);
        long weakCount = countCapabilityScores(studentIds, majorId, true);
        return overview("fusion", majorId, List.of(
                metric("fusion_relation_count", "融合关系", relationCount, "条"),
                metric("capability_score_count", "能力得分", scoreCount, "条"),
                metric("weak_point_count", "能力短板", weakCount, "项")
        ));
    }

    public StatisticsOverviewVO learningEffectStatistics(Long majorId) {
        List<Long> studentIds = studentIdsByMajor(majorId);
        long recordCount = countLearningRecords(studentIds, majorId);
        long quizCount = countQuizAttempts(studentIds, majorId);
        long wrongCount = countWrongQuestions(studentIds, majorId);
        long evaluationCount = countEvaluations(studentIds, majorId);
        return overview("learning_effect", majorId, List.of(
                metric("learning_record_count", "学习记录", recordCount, "条"),
                metric("quiz_attempt_count", "答题次数", quizCount, "次"),
                metric("wrong_question_count", "错题数", wrongCount, "题"),
                metric("evaluation_count", "评估报告", evaluationCount, "份")
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    public ExportRecordVO createExport(ExportRequest request) {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        ExportRecord record = new ExportRecord();
        record.setExportType(request.getExportType());
        record.setExportScope(request.getExportScope());
        record.setMajorId(request.getMajorId());
        record.setExportedBy(currentUser.getUserId());
        record.setDesensitized(request.getDesensitized() == null || request.getDesensitized());
        record.setExportStatus("queued");
        record.setStatus("active");
        record.setCreatedBy(currentUser.getUserId());
        exportRecordMapper.insert(record);
        auditService.operation("statistics_analysis", "create_export", "export_record", record.getId(), "success", "创建统计导出记录，文件生成待异步任务处理");
        return toVO(record);
    }

    public PageResult<ExportRecordVO> pageExports(PageQuery query) {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        Page<ExportRecord> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<ExportRecord> wrapper = new LambdaQueryWrapper<ExportRecord>()
                .eq(ExportRecord::getExportedBy, currentUser.getUserId())
                .isNull(ExportRecord::getDeletedAt)
                .orderByDesc(ExportRecord::getCreatedAt);
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(ExportRecord::getExportStatus, query.getStatus());
        }
        Page<ExportRecord> result = exportRecordMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toVO).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    public ExportRecordVO getExport(Long id) {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        ExportRecord record = exportRecordMapper.selectById(id);
        if (record == null || record.getDeletedAt() != null || !currentUser.getUserId().equals(record.getExportedBy())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "导出记录不存在");
        }
        return toVO(record);
    }

    private long countStudents(Long majorId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<Student>().isNull(Student::getDeletedAt);
        if (majorId != null) {
            wrapper.eq(Student::getMajorId, majorId);
        }
        return studentMapper.selectCount(wrapper);
    }

    private List<Long> studentIdsByMajor(Long majorId) {
        if (majorId == null) {
            return null;
        }
        return studentMapper.selectList(new LambdaQueryWrapper<Student>()
                        .eq(Student::getMajorId, majorId)
                        .isNull(Student::getDeletedAt))
                .stream()
                .map(Student::getId)
                .toList();
    }

    private long countProfiles(List<Long> studentIds, Long majorId) {
        LambdaQueryWrapper<StudentProfile> wrapper = new LambdaQueryWrapper<StudentProfile>().isNull(StudentProfile::getDeletedAt);
        if (majorId != null) {
            if (studentIds == null || studentIds.isEmpty()) {
                return 0;
            }
            wrapper.in(StudentProfile::getStudentId, studentIds);
        }
        return profileMapper.selectCount(wrapper);
    }

    private long countCapabilityScores(List<Long> studentIds, Long majorId, boolean weakOnly) {
        LambdaQueryWrapper<StudentCapabilityScore> wrapper = new LambdaQueryWrapper<StudentCapabilityScore>().isNull(StudentCapabilityScore::getDeletedAt);
        if (majorId != null) {
            if (studentIds == null || studentIds.isEmpty()) {
                return 0;
            }
            wrapper.in(StudentCapabilityScore::getStudentId, studentIds);
        }
        if (weakOnly) {
            wrapper.and(w -> w.lt(StudentCapabilityScore::getScore, BigDecimal.valueOf(60))
                    .or()
                    .in(StudentCapabilityScore::getMasteryStatus, List.of("weak", "not_mastered")));
        }
        return capabilityScoreMapper.selectCount(wrapper);
    }

    private long countLearningRecords(List<Long> studentIds, Long majorId) {
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<LearningRecord>().isNull(LearningRecord::getDeletedAt);
        if (!applyStudentFilter(wrapper, studentIds, majorId, LearningRecord::getStudentId)) {
            return 0;
        }
        return learningRecordMapper.selectCount(wrapper);
    }

    private long countQuizAttempts(List<Long> studentIds, Long majorId) {
        LambdaQueryWrapper<QuizAttempt> wrapper = new LambdaQueryWrapper<QuizAttempt>().isNull(QuizAttempt::getDeletedAt);
        if (!applyStudentFilter(wrapper, studentIds, majorId, QuizAttempt::getStudentId)) {
            return 0;
        }
        return quizAttemptMapper.selectCount(wrapper);
    }

    private long countWrongQuestions(List<Long> studentIds, Long majorId) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<WrongQuestion>().isNull(WrongQuestion::getDeletedAt);
        if (!applyStudentFilter(wrapper, studentIds, majorId, WrongQuestion::getStudentId)) {
            return 0;
        }
        return wrongQuestionMapper.selectCount(wrapper);
    }

    private long countEvaluations(List<Long> studentIds, Long majorId) {
        LambdaQueryWrapper<LearningEvaluation> wrapper = new LambdaQueryWrapper<LearningEvaluation>().isNull(LearningEvaluation::getDeletedAt);
        if (!applyStudentFilter(wrapper, studentIds, majorId, LearningEvaluation::getStudentId)) {
            return 0;
        }
        return evaluationMapper.selectCount(wrapper);
    }

    private <T> boolean applyStudentFilter(
            LambdaQueryWrapper<T> wrapper,
            List<Long> studentIds,
            Long majorId,
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> column
    ) {
        if (majorId == null) {
            return true;
        }
        if (studentIds == null || studentIds.isEmpty()) {
            return false;
        }
        wrapper.in(column, studentIds);
        return true;
    }

    private StatisticsOverviewVO overview(String type, Long majorId, List<DashboardMetricVO> metrics) {
        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setStatisticsType(type);
        vo.setMajorId(majorId);
        vo.setMetrics(metrics);
        return vo;
    }

    private DashboardMetricVO metric(String code, String name, Object value, String unit) {
        return new DashboardMetricVO(code, name, value, unit);
    }

    private BigDecimal percent(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    private ExportRecordVO toVO(ExportRecord record) {
        ExportRecordVO vo = new ExportRecordVO();
        vo.setId(record.getId());
        vo.setExportType(record.getExportType());
        vo.setExportScope(record.getExportScope());
        vo.setMajorId(record.getMajorId());
        vo.setExportedBy(record.getExportedBy());
        vo.setFileUrl(record.getFileUrl());
        vo.setDesensitized(record.getDesensitized());
        vo.setExportStatus(record.getExportStatus());
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }
}
