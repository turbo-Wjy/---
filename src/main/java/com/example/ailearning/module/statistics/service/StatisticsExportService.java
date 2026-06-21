package com.example.ailearning.module.statistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.crypto.AesGcmCryptoService;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.base.entity.Major;
import com.example.ailearning.module.base.entity.SchoolClass;
import com.example.ailearning.module.base.mapper.MajorMapper;
import com.example.ailearning.module.base.mapper.SchoolClassMapper;
import com.example.ailearning.module.certificate.entity.CertificateResult;
import com.example.ailearning.module.certificate.mapper.CertificateResultMapper;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.mapper.CompetitionResultMapper;
import com.example.ailearning.module.course.entity.LearningRecord;
import com.example.ailearning.module.course.mapper.LearningRecordMapper;
import com.example.ailearning.module.evaluation.entity.LearningEvaluation;
import com.example.ailearning.module.evaluation.mapper.LearningEvaluationMapper;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.statistics.dto.StatisticsExportRequest;
import com.example.ailearning.module.statistics.entity.ExportRecord;
import com.example.ailearning.module.statistics.mapper.ExportRecordMapper;
import com.example.ailearning.module.statistics.vo.ExportRecordVO;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.mapper.StudentMapper;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsExportService {
    private static final Set<String> SUPPORTED_TYPES = Set.of(
            "major_summary",
            "student_profile_summary",
            "course_learning_progress",
            "competition_award_statistics",
            "certificate_completion_statistics",
            "learning_effect_statistics"
    );

    private final ExportRecordMapper exportRecordMapper;
    private final MajorMapper majorMapper;
    private final SchoolClassMapper classMapper;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final StudentProfileMapper profileMapper;
    private final LearningRecordMapper learningRecordMapper;
    private final CompetitionResultMapper competitionResultMapper;
    private final CertificateResultMapper certificateResultMapper;
    private final LearningEvaluationMapper evaluationMapper;
    private final AesGcmCryptoService cryptoService;
    private final AuditService auditService;

    public StatisticsExportService(
            ExportRecordMapper exportRecordMapper,
            MajorMapper majorMapper,
            SchoolClassMapper classMapper,
            StudentMapper studentMapper,
            UserMapper userMapper,
            StudentProfileMapper profileMapper,
            LearningRecordMapper learningRecordMapper,
            CompetitionResultMapper competitionResultMapper,
            CertificateResultMapper certificateResultMapper,
            LearningEvaluationMapper evaluationMapper,
            AesGcmCryptoService cryptoService,
            AuditService auditService
    ) {
        this.exportRecordMapper = exportRecordMapper;
        this.majorMapper = majorMapper;
        this.classMapper = classMapper;
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
        this.profileMapper = profileMapper;
        this.learningRecordMapper = learningRecordMapper;
        this.competitionResultMapper = competitionResultMapper;
        this.certificateResultMapper = certificateResultMapper;
        this.evaluationMapper = evaluationMapper;
        this.cryptoService = cryptoService;
        this.auditService = auditService;
    }

    @Transactional(rollbackFor = Exception.class)
    public ExportRecordVO export(StatisticsExportRequest request) {
        if (!SUPPORTED_TYPES.contains(request.getExportType())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的导出类型");
        }
        Major major = majorMapper.selectById(request.getMajorId());
        if (major == null || major.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专业不存在");
        }
        Boolean desensitized = request.getDesensitized() == null || request.getDesensitized();
        ExportRecord record = new ExportRecord();
        record.setExportType(request.getExportType());
        record.setExportScope("major");
        record.setMajorId(request.getMajorId());
        record.setExportedBy(CurrentUserHolder.getRequired().getUserId());
        record.setDesensitized(desensitized);
        record.setExportStatus("processing");
        record.setStatus("active");
        record.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        exportRecordMapper.insert(record);

        try {
            List<List<String>> rows = buildRows(request.getExportType(), request.getMajorId(), desensitized);
            String fileUrl = writeCsv(request.getExportType(), request.getMajorId(), rows);
            record.setFileUrl(fileUrl);
            record.setExportStatus("finished");
            exportRecordMapper.updateById(record);
            auditService.operation("statistics_analysis", "export_statistics", "export_record", record.getId(), "success", "导出专业统计数据：" + request.getExportType());
            return toVO(record, Math.max(0, rows.size() - 1));
        } catch (RuntimeException e) {
            record.setExportStatus("failed");
            exportRecordMapper.updateById(record);
            auditService.operation("statistics_analysis", "export_statistics", "export_record", record.getId(), "failed", "导出专业统计数据失败");
            throw e;
        }
    }

    public PageResult<ExportRecordVO> records(PageQuery query, Long majorId, String exportType) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ExportRecord> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<ExportRecord> wrapper = new LambdaQueryWrapper<ExportRecord>()
                .isNull(ExportRecord::getDeletedAt)
                .orderByDesc(ExportRecord::getCreatedAt);
        if (majorId != null) {
            wrapper.eq(ExportRecord::getMajorId, majorId);
        }
        if (exportType != null && !exportType.isBlank()) {
            wrapper.eq(ExportRecord::getExportType, exportType);
        }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ExportRecord> result = exportRecordMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(record -> toVO(record, null)).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    private List<List<String>> buildRows(String exportType, Long majorId, boolean desensitized) {
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                .eq(Student::getMajorId, majorId)
                .isNull(Student::getDeletedAt)
                .orderByAsc(Student::getClassId)
                .orderByAsc(Student::getStudentNo));
        if (students.isEmpty()) {
            return List.of(baseHeader(exportType));
        }
        ExportContext context = context(students);
        return switch (exportType) {
            case "student_profile_summary" -> profileRows(students, context, desensitized);
            case "course_learning_progress" -> courseRows(students, context, desensitized);
            case "competition_award_statistics" -> competitionRows(students, context, desensitized);
            case "certificate_completion_statistics" -> certificateRows(students, context, desensitized);
            case "learning_effect_statistics" -> evaluationRows(students, context, desensitized);
            default -> summaryRows(students, context, desensitized);
        };
    }

    private ExportContext context(List<Student> students) {
        List<Long> studentIds = students.stream().map(Student::getId).toList();
        List<Long> userIds = students.stream().map(Student::getUserId).toList();
        ExportContext context = new ExportContext();
        context.users = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds).isNull(User::getDeletedAt))
                .stream().collect(Collectors.toMap(User::getId, Function.identity(), (left, right) -> left));
        context.classes = classMapper.selectList(new LambdaQueryWrapper<SchoolClass>().isNull(SchoolClass::getDeletedAt))
                .stream().collect(Collectors.toMap(SchoolClass::getId, Function.identity(), (left, right) -> left));
        context.profiles = latestByStudent(profileMapper.selectList(new LambdaQueryWrapper<StudentProfile>()
                .in(StudentProfile::getStudentId, studentIds)
                .isNull(StudentProfile::getDeletedAt)), StudentProfile::getStudentId);
        context.learningRecords = learningRecordMapper.selectList(new LambdaQueryWrapper<LearningRecord>()
                .in(LearningRecord::getStudentId, studentIds)
                .isNull(LearningRecord::getDeletedAt))
                .stream().collect(Collectors.groupingBy(LearningRecord::getStudentId));
        context.competitionResults = competitionResultMapper.selectList(new LambdaQueryWrapper<CompetitionResult>()
                .in(CompetitionResult::getStudentId, studentIds)
                .isNull(CompetitionResult::getDeletedAt))
                .stream().collect(Collectors.groupingBy(CompetitionResult::getStudentId));
        context.certificateResults = certificateResultMapper.selectList(new LambdaQueryWrapper<CertificateResult>()
                .in(CertificateResult::getStudentId, studentIds)
                .isNull(CertificateResult::getDeletedAt))
                .stream().collect(Collectors.groupingBy(CertificateResult::getStudentId));
        context.evaluations = latestByStudent(evaluationMapper.selectList(new LambdaQueryWrapper<LearningEvaluation>()
                .in(LearningEvaluation::getStudentId, studentIds)
                .isNull(LearningEvaluation::getDeletedAt)), LearningEvaluation::getStudentId);
        return context;
    }

    private <T> Map<Long, T> latestByStudent(List<T> items, Function<T, Long> studentIdGetter) {
        return items.stream().collect(Collectors.groupingBy(studentIdGetter)).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .max(Comparator.comparing(item -> {
                            if (item instanceof StudentProfile profile) {
                                return profile.getCreatedAt();
                            }
                            if (item instanceof LearningEvaluation evaluation) {
                                return evaluation.getCreatedAt();
                            }
                            return LocalDateTime.MIN;
                        }))
                        .orElse(null)));
    }

    private List<List<String>> summaryRows(List<Student> students, ExportContext context, boolean desensitized) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("学号", "姓名", "班级", "年级", "画像完整度", "学习记录数", "学习时长分钟", "完成学习记录数", "竞赛通过数", "证书通过数", "最近评估摘要"));
        for (Student student : students) {
            LearningStats learning = learningStats(context.learningRecords.get(student.getId()));
            rows.add(baseStudentCells(student, context, desensitized, List.of(
                    profileCompleteness(context.profiles.get(student.getId())),
                    String.valueOf(learning.recordCount),
                    String.valueOf(learning.durationMinutes),
                    String.valueOf(learning.completedCount),
                    String.valueOf(approvedCompetitionCount(context.competitionResults.get(student.getId()))),
                    String.valueOf(approvedCertificateCount(context.certificateResults.get(student.getId()))),
                    evaluationSummary(context.evaluations.get(student.getId()))
            )));
        }
        return rows;
    }

    private List<List<String>> profileRows(List<Student> students, ExportContext context, boolean desensitized) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("学号", "姓名", "班级", "年级", "画像版本", "画像完整度", "画像摘要"));
        for (Student student : students) {
            StudentProfile profile = context.profiles.get(student.getId());
            rows.add(baseStudentCells(student, context, desensitized, List.of(
                    profile == null ? "" : String.valueOf(profile.getProfileVersion()),
                    profileCompleteness(profile),
                    profile == null ? "" : safeDecrypt(profile.getProfileSummaryEncrypted(), profile.getProfileSummaryIv())
            )));
        }
        return rows;
    }

    private List<List<String>> courseRows(List<Student> students, ExportContext context, boolean desensitized) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("学号", "姓名", "班级", "年级", "学习记录数", "浏览/下载/学习时长分钟", "完成学习记录数", "完成率"));
        for (Student student : students) {
            LearningStats stats = learningStats(context.learningRecords.get(student.getId()));
            rows.add(baseStudentCells(student, context, desensitized, List.of(
                    String.valueOf(stats.recordCount),
                    String.valueOf(stats.durationMinutes),
                    String.valueOf(stats.completedCount),
                    stats.recordCount == 0 ? "0%" : String.format("%.2f%%", stats.completedCount * 100.0 / stats.recordCount)
            )));
        }
        return rows;
    }

    private List<List<String>> competitionRows(List<Student> students, ExportContext context, boolean desensitized) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("学号", "姓名", "班级", "年级", "竞赛成果数", "审核通过数", "待审核数", "打回数"));
        for (Student student : students) {
            List<CompetitionResult> results = context.competitionResults.getOrDefault(student.getId(), List.of());
            StatusStats stats = statusStats(results.stream().map(CompetitionResult::getReviewStatus).toList());
            rows.add(baseStudentCells(student, context, desensitized, List.of(String.valueOf(stats.total), String.valueOf(stats.approved), String.valueOf(stats.pending), String.valueOf(stats.rejected))));
        }
        return rows;
    }

    private List<List<String>> certificateRows(List<Student> students, ExportContext context, boolean desensitized) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("学号", "姓名", "班级", "年级", "证书成果数", "审核通过数", "待审核数", "打回数"));
        for (Student student : students) {
            List<CertificateResult> results = context.certificateResults.getOrDefault(student.getId(), List.of());
            StatusStats stats = statusStats(results.stream().map(CertificateResult::getReviewStatus).toList());
            rows.add(baseStudentCells(student, context, desensitized, List.of(String.valueOf(stats.total), String.valueOf(stats.approved), String.valueOf(stats.pending), String.valueOf(stats.rejected))));
        }
        return rows;
    }

    private List<List<String>> evaluationRows(List<Student> students, ExportContext context, boolean desensitized) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("学号", "姓名", "班级", "年级", "最近评估时间", "评估摘要", "评分明细JSON", "建议JSON"));
        for (Student student : students) {
            LearningEvaluation evaluation = context.evaluations.get(student.getId());
            rows.add(baseStudentCells(student, context, desensitized, List.of(
                    evaluation == null ? "" : String.valueOf(evaluation.getCreatedAt()),
                    evaluationSummary(evaluation),
                    evaluation == null ? "" : evaluation.getScoreJson(),
                    evaluation == null ? "" : evaluation.getSuggestionJson()
            )));
        }
        return rows;
    }

    private List<String> baseHeader(String exportType) {
        return List.of("导出类型", "提示");
    }

    private List<String> baseStudentCells(Student student, ExportContext context, boolean desensitized, List<String> tail) {
        User user = context.users.get(student.getUserId());
        SchoolClass schoolClass = context.classes.get(student.getClassId());
        List<String> row = new ArrayList<>();
        row.add(desensitized ? maskStudentNo(student.getStudentNo()) : student.getStudentNo());
        row.add(desensitized ? maskName(user == null ? "" : user.getRealName()) : user == null ? "" : user.getRealName());
        row.add(schoolClass == null ? "" : schoolClass.getName());
        row.add(student.getGrade());
        row.addAll(tail);
        return row;
    }

    private LearningStats learningStats(List<LearningRecord> records) {
        List<LearningRecord> safeRecords = records == null ? List.of() : records;
        LearningStats stats = new LearningStats();
        stats.recordCount = safeRecords.size();
        stats.completedCount = safeRecords.stream().filter(record -> Boolean.TRUE.equals(record.getCompleted())).count();
        stats.durationMinutes = safeRecords.stream()
                .map(LearningRecord::getDurationSeconds)
                .filter(value -> value != null)
                .mapToLong(Integer::longValue)
                .sum() / 60;
        return stats;
    }

    private StatusStats statusStats(List<String> statuses) {
        StatusStats stats = new StatusStats();
        stats.total = statuses.size();
        stats.approved = statuses.stream().filter("approved"::equals).count();
        stats.pending = statuses.stream().filter("pending"::equals).count();
        stats.rejected = statuses.stream().filter("rejected"::equals).count();
        return stats;
    }

    private long approvedCompetitionCount(List<CompetitionResult> results) {
        return results == null ? 0 : results.stream().filter(result -> "approved".equals(result.getReviewStatus())).count();
    }

    private long approvedCertificateCount(List<CertificateResult> results) {
        return results == null ? 0 : results.stream().filter(result -> "approved".equals(result.getReviewStatus())).count();
    }

    private String profileCompleteness(StudentProfile profile) {
        return profile == null || profile.getCompletenessScore() == null ? "" : profile.getCompletenessScore().toPlainString();
    }

    private String evaluationSummary(LearningEvaluation evaluation) {
        return evaluation == null ? "" : evaluation.getEvaluationSummary();
    }

    private String safeDecrypt(String encrypted, String iv) {
        try {
            return cryptoService.decrypt(encrypted, iv);
        } catch (BusinessException e) {
            return "";
        }
    }

    private String maskStudentNo(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return value.substring(0, Math.min(4, value.length())) + "****" + value.substring(value.length() - 2);
    }

    private String maskName(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        if (value.length() == 1) {
            return "*";
        }
        return value.charAt(0) + "*";
    }

    private String writeCsv(String exportType, Long majorId, List<List<String>> rows) {
        try {
            Path dir = Path.of("exports", "statistics");
            Files.createDirectories(dir);
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
            Path file = dir.resolve(exportType + "_major_" + majorId + "_" + timestamp + ".csv");
            StringBuilder builder = new StringBuilder("\uFEFF");
            for (List<String> row : rows) {
                builder.append(row.stream().map(this::csvCell).collect(Collectors.joining(","))).append('\n');
            }
            Files.writeString(file, builder.toString(), StandardCharsets.UTF_8);
            return file.toString();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "导出文件生成失败");
        }
    }

    private String csvCell(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private ExportRecordVO toVO(ExportRecord record, Integer rowCount) {
        ExportRecordVO vo = new ExportRecordVO();
        vo.setId(record.getId());
        vo.setExportType(record.getExportType());
        vo.setExportScope(record.getExportScope());
        vo.setMajorId(record.getMajorId());
        vo.setExportedBy(record.getExportedBy());
        vo.setFileUrl(record.getFileUrl());
        vo.setDesensitized(record.getDesensitized());
        vo.setExportStatus(record.getExportStatus());
        vo.setStatus(record.getStatus());
        vo.setRowCount(rowCount);
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }

    private static class ExportContext {
        private Map<Long, User> users = new LinkedHashMap<>();
        private Map<Long, SchoolClass> classes = new LinkedHashMap<>();
        private Map<Long, StudentProfile> profiles = new LinkedHashMap<>();
        private Map<Long, List<LearningRecord>> learningRecords = new LinkedHashMap<>();
        private Map<Long, List<CompetitionResult>> competitionResults = new LinkedHashMap<>();
        private Map<Long, List<CertificateResult>> certificateResults = new LinkedHashMap<>();
        private Map<Long, LearningEvaluation> evaluations = new LinkedHashMap<>();
    }

    private static class LearningStats {
        private long recordCount;
        private long completedCount;
        private long durationMinutes;
    }

    private static class StatusStats {
        private long total;
        private long approved;
        private long pending;
        private long rejected;
    }
}
