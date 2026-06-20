package com.example.ailearning.module.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.course.entity.LearningRecord;
import com.example.ailearning.module.course.entity.QuizAttempt;
import com.example.ailearning.module.course.entity.WrongQuestion;
import com.example.ailearning.module.course.mapper.LearningRecordMapper;
import com.example.ailearning.module.course.mapper.QuizAttemptMapper;
import com.example.ailearning.module.course.mapper.WrongQuestionMapper;
import com.example.ailearning.module.evaluation.dto.LearningEvaluationGenerateRequest;
import com.example.ailearning.module.evaluation.entity.LearningEvaluation;
import com.example.ailearning.module.evaluation.mapper.LearningEvaluationMapper;
import com.example.ailearning.module.evaluation.vo.LearningEvaluationVO;
import com.example.ailearning.module.fusion.entity.StudentCapabilityScore;
import com.example.ailearning.module.fusion.mapper.StudentCapabilityScoreMapper;
import com.example.ailearning.module.learning.entity.LearningPath;
import com.example.ailearning.module.learning.entity.LearningPathStep;
import com.example.ailearning.module.learning.entity.ResourceRecommendation;
import com.example.ailearning.module.learning.mapper.LearningPathMapper;
import com.example.ailearning.module.learning.mapper.LearningPathStepMapper;
import com.example.ailearning.module.learning.mapper.ResourceRecommendationMapper;
import com.example.ailearning.module.student.service.StudentContextService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearningEvaluationService {
    private final StudentContextService studentContextService;
    private final LearningEvaluationMapper evaluationMapper;
    private final LearningPathMapper pathMapper;
    private final LearningPathStepMapper stepMapper;
    private final LearningRecordMapper learningRecordMapper;
    private final QuizAttemptMapper quizAttemptMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final ResourceRecommendationMapper recommendationMapper;
    private final StudentCapabilityScoreMapper capabilityScoreMapper;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public LearningEvaluationService(
            StudentContextService studentContextService,
            LearningEvaluationMapper evaluationMapper,
            LearningPathMapper pathMapper,
            LearningPathStepMapper stepMapper,
            LearningRecordMapper learningRecordMapper,
            QuizAttemptMapper quizAttemptMapper,
            WrongQuestionMapper wrongQuestionMapper,
            ResourceRecommendationMapper recommendationMapper,
            StudentCapabilityScoreMapper capabilityScoreMapper,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.studentContextService = studentContextService;
        this.evaluationMapper = evaluationMapper;
        this.pathMapper = pathMapper;
        this.stepMapper = stepMapper;
        this.learningRecordMapper = learningRecordMapper;
        this.quizAttemptMapper = quizAttemptMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.recommendationMapper = recommendationMapper;
        this.capabilityScoreMapper = capabilityScoreMapper;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningEvaluationVO generate(LearningEvaluationGenerateRequest request) {
        Long studentId = studentContextService.currentStudentIdRequired();
        EvaluationStats stats = collectStats(studentId);
        BigDecimal overallScore = overallScore(stats);
        List<String> suggestions = suggestions(stats);

        LearningEvaluation evaluation = new LearningEvaluation();
        evaluation.setStudentId(studentId);
        evaluation.setSourceType(request.getSourceType() == null || request.getSourceType().isBlank() ? "overall" : request.getSourceType());
        evaluation.setSourceId(request.getSourceId());
        evaluation.setEvaluationSummary(summary(overallScore, stats));
        evaluation.setScoreJson(toJson(scoreDetail(overallScore, stats)));
        evaluation.setSuggestionJson(toJson(suggestions));
        evaluation.setStatus("active");
        evaluation.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        evaluationMapper.insert(evaluation);

        syncKnowledgePointScores(studentId, evaluation.getId(), stats.quizAttempts);
        auditService.operation("ai_learning_center", "generate_learning_evaluation", "learning_evaluation", evaluation.getId(), "success", "基于学习路径、学习记录、答题和错题生成学习效果评估");
        return toVO(evaluation);
    }

    private Map<String, Object> scoreDetail(BigDecimal overallScore, EvaluationStats stats) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("overallScore", overallScore);
        detail.put("pathProgressScore", stats.pathProgressScore);
        detail.put("learningBehaviorScore", stats.learningBehaviorScore);
        detail.put("quizScore", stats.quizScore);
        detail.put("wrongQuestionPenalty", stats.wrongQuestionPenalty);
        detail.put("pathStepTotal", stats.pathStepTotal);
        detail.put("pathStepCompleted", stats.pathStepCompleted);
        detail.put("learningRecordCount", stats.learningRecordCount);
        detail.put("totalDurationSeconds", stats.totalDurationSeconds);
        detail.put("quizAttemptCount", stats.quizAttemptCount);
        detail.put("quizCorrectCount", stats.quizCorrectCount);
        detail.put("wrongQuestionCount", stats.wrongQuestionCount);
        detail.put("recommendationCount", stats.recommendationCount);
        return detail;
    }

    public List<LearningEvaluationVO> myEvaluations() {
        Long studentId = studentContextService.currentStudentIdRequired();
        return evaluationMapper.selectList(new LambdaQueryWrapper<LearningEvaluation>()
                        .eq(LearningEvaluation::getStudentId, studentId)
                        .isNull(LearningEvaluation::getDeletedAt)
                        .orderByDesc(LearningEvaluation::getCreatedAt))
                .stream().map(this::toVO).toList();
    }

    private EvaluationStats collectStats(Long studentId) {
        List<LearningPath> paths = pathMapper.selectList(new LambdaQueryWrapper<LearningPath>()
                .eq(LearningPath::getStudentId, studentId)
                .isNull(LearningPath::getDeletedAt));
        List<Long> pathIds = paths.stream().map(LearningPath::getId).toList();
        List<LearningPathStep> steps = pathIds.isEmpty()
                ? List.of()
                : stepMapper.selectList(new LambdaQueryWrapper<LearningPathStep>()
                .in(LearningPathStep::getPathId, pathIds)
                .isNull(LearningPathStep::getDeletedAt));
        List<LearningRecord> records = learningRecordMapper.selectList(new LambdaQueryWrapper<LearningRecord>()
                .eq(LearningRecord::getStudentId, studentId)
                .isNull(LearningRecord::getDeletedAt));
        List<QuizAttempt> quizzes = quizAttemptMapper.selectList(new LambdaQueryWrapper<QuizAttempt>()
                .eq(QuizAttempt::getStudentId, studentId)
                .isNull(QuizAttempt::getDeletedAt));
        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectList(new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getStudentId, studentId)
                .isNull(WrongQuestion::getDeletedAt));
        List<ResourceRecommendation> recommendations = recommendationMapper.selectList(new LambdaQueryWrapper<ResourceRecommendation>()
                .eq(ResourceRecommendation::getStudentId, studentId)
                .isNull(ResourceRecommendation::getDeletedAt));

        EvaluationStats stats = new EvaluationStats();
        stats.pathStepTotal = steps.size();
        stats.pathStepCompleted = steps.stream().filter(step -> "completed".equals(step.getCompletionStatus())).count();
        stats.learningRecordCount = records.size();
        stats.completedLearningRecordCount = records.stream().filter(record -> Boolean.TRUE.equals(record.getCompleted())).count();
        stats.totalDurationSeconds = records.stream().map(LearningRecord::getDurationSeconds).filter(v -> v != null).mapToLong(Integer::longValue).sum();
        stats.quizAttempts = quizzes;
        stats.quizAttemptCount = quizzes.size();
        stats.quizCorrectCount = quizzes.stream().filter(quiz -> Boolean.TRUE.equals(quiz.getCorrect())).count();
        stats.averageQuizScore = quizzes.stream()
                .filter(quiz -> quiz.getScore() != null)
                .map(QuizAttempt::getScore)
                .mapToDouble(BigDecimal::doubleValue)
                .summaryStatistics();
        stats.wrongQuestionCount = wrongQuestions.size();
        stats.recommendationCount = recommendations.size();
        stats.unreadRecommendationCount = recommendations.stream().filter(r -> "unread".equals(r.getViewStatus())).count();

        stats.pathProgressScore = stats.pathStepTotal == 0 ? BigDecimal.ZERO : percent(stats.pathStepCompleted, stats.pathStepTotal);
        stats.learningBehaviorScore = learningBehaviorScore(stats);
        stats.quizScore = quizScore(stats);
        stats.wrongQuestionPenalty = BigDecimal.valueOf(Math.min(20, stats.wrongQuestionCount * 2L));
        return stats;
    }

    private BigDecimal overallScore(EvaluationStats stats) {
        BigDecimal score = stats.pathProgressScore.multiply(BigDecimal.valueOf(0.30))
                .add(stats.learningBehaviorScore.multiply(BigDecimal.valueOf(0.20)))
                .add(stats.quizScore.multiply(BigDecimal.valueOf(0.40)))
                .add(recommendationScore(stats).multiply(BigDecimal.valueOf(0.10)))
                .subtract(stats.wrongQuestionPenalty.multiply(BigDecimal.valueOf(0.20)));
        if (score.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (score.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100);
        }
        return score.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal learningBehaviorScore(EvaluationStats stats) {
        BigDecimal durationScore = BigDecimal.valueOf(Math.min(100, stats.totalDurationSeconds / 3600.0 * 20));
        BigDecimal completionScore = stats.learningRecordCount == 0 ? BigDecimal.ZERO : percent(stats.completedLearningRecordCount, stats.learningRecordCount);
        return durationScore.multiply(BigDecimal.valueOf(0.4)).add(completionScore.multiply(BigDecimal.valueOf(0.6))).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal quizScore(EvaluationStats stats) {
        if (stats.quizAttemptCount == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal correctness = percent(stats.quizCorrectCount, stats.quizAttemptCount);
        BigDecimal average = stats.averageQuizScore.getCount() == 0 ? correctness : BigDecimal.valueOf(stats.averageQuizScore.getAverage());
        return correctness.multiply(BigDecimal.valueOf(0.6)).add(average.multiply(BigDecimal.valueOf(0.4))).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal recommendationScore(EvaluationStats stats) {
        if (stats.recommendationCount == 0) {
            return BigDecimal.ZERO;
        }
        long read = stats.recommendationCount - stats.unreadRecommendationCount;
        return percent(read, stats.recommendationCount);
    }

    private List<String> suggestions(EvaluationStats stats) {
        List<String> suggestions = new ArrayList<>();
        if (stats.pathStepTotal == 0) {
            suggestions.add("建议先生成并接受个性化学习路径，让系统形成阶段性任务安排。");
        } else if (stats.pathProgressScore.compareTo(BigDecimal.valueOf(60)) < 0) {
            suggestions.add("学习路径完成度偏低，建议优先完成未完成步骤。");
        }
        if (stats.learningRecordCount == 0) {
            suggestions.add("暂无学习行为记录，建议从推荐资源或课程资料开始学习。");
        }
        if (stats.quizAttemptCount == 0) {
            suggestions.add("暂无答题记录，建议完成题库练习以校准知识掌握情况。");
        } else if (stats.quizScore.compareTo(BigDecimal.valueOf(70)) < 0) {
            suggestions.add("答题表现仍有提升空间，建议结合错题回看相关知识点。");
        }
        if (stats.wrongQuestionCount > 0) {
            suggestions.add("当前存在 " + stats.wrongQuestionCount + " 道错题，建议完成错题复盘。");
        }
        if (stats.unreadRecommendationCount > 0) {
            suggestions.add("有 " + stats.unreadRecommendationCount + " 个推荐资源未查看，建议结合学习路径使用。");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("当前学习状态较好，建议继续保持路径推进并参与项目实训。");
        }
        return suggestions;
    }

    private void syncKnowledgePointScores(Long studentId, Long evaluationId, List<QuizAttempt> quizzes) {
        Map<Long, List<QuizAttempt>> byKnowledgePoint = quizzes.stream()
                .filter(quiz -> quiz.getKnowledgePointId() != null && quiz.getScore() != null)
                .collect(Collectors.groupingBy(QuizAttempt::getKnowledgePointId));
        for (Map.Entry<Long, List<QuizAttempt>> entry : byKnowledgePoint.entrySet()) {
            BigDecimal average = BigDecimal.valueOf(entry.getValue().stream()
                    .map(QuizAttempt::getScore)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .orElse(0)).setScale(2, RoundingMode.HALF_UP);
            StudentCapabilityScore score = capabilityScoreMapper.selectOne(new LambdaQueryWrapper<StudentCapabilityScore>()
                    .eq(StudentCapabilityScore::getStudentId, studentId)
                    .eq(StudentCapabilityScore::getTargetType, "course_knowledge_point")
                    .eq(StudentCapabilityScore::getTargetId, entry.getKey())
                    .isNull(StudentCapabilityScore::getDeletedAt)
                    .last("LIMIT 1"));
            if (score == null) {
                score = new StudentCapabilityScore();
                score.setStudentId(studentId);
                score.setTargetType("course_knowledge_point");
                score.setTargetId(entry.getKey());
                score.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
            }
            score.setScore(average);
            score.setMasteryStatus(masteryStatus(average));
            score.setSourceType("learning_evaluation");
            score.setSourceId(evaluationId);
            score.setEvidenceJson(toJson(Map.of("quizAttemptCount", entry.getValue().size(), "averageScore", average)));
            score.setEvaluatedAt(LocalDateTime.now());
            score.setStatus("active");
            if (score.getId() == null) {
                capabilityScoreMapper.insert(score);
            } else {
                capabilityScoreMapper.updateById(score);
            }
        }
    }

    private String masteryStatus(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(85)) >= 0) {
            return "mastered";
        }
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "qualified";
        }
        if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "developing";
        }
        return "weak";
    }

    private String summary(BigDecimal overallScore, EvaluationStats stats) {
        return "本次学习效果综合得分 " + overallScore + " 分。路径完成 " + stats.pathStepCompleted + "/" + stats.pathStepTotal
                + "，学习记录 " + stats.learningRecordCount + " 条，答题 " + stats.quizAttemptCount + " 次，错题 " + stats.wrongQuestionCount + " 道。";
    }

    private LearningEvaluationVO toVO(LearningEvaluation evaluation) {
        LearningEvaluationVO vo = new LearningEvaluationVO();
        vo.setId(evaluation.getId());
        vo.setStudentId(evaluation.getStudentId());
        vo.setSourceType(evaluation.getSourceType());
        vo.setSourceId(evaluation.getSourceId());
        vo.setEvaluationSummary(evaluation.getEvaluationSummary());
        vo.setScore(evaluation.getScoreJson());
        vo.setSuggestion(evaluation.getSuggestionJson());
        vo.setStatus(evaluation.getStatus());
        vo.setCreatedAt(evaluation.getCreatedAt());
        return vo;
    }

    private BigDecimal percent(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "JSON 字段格式不正确");
        }
    }

    private static class EvaluationStats {
        private long pathStepTotal;
        private long pathStepCompleted;
        private long learningRecordCount;
        private long completedLearningRecordCount;
        private long totalDurationSeconds;
        private long quizAttemptCount;
        private long quizCorrectCount;
        private long wrongQuestionCount;
        private long recommendationCount;
        private long unreadRecommendationCount;
        private DoubleSummaryStatistics averageQuizScore = new DoubleSummaryStatistics();
        private List<QuizAttempt> quizAttempts = List.of();
        private BigDecimal pathProgressScore = BigDecimal.ZERO;
        private BigDecimal learningBehaviorScore = BigDecimal.ZERO;
        private BigDecimal quizScore = BigDecimal.ZERO;
        private BigDecimal wrongQuestionPenalty = BigDecimal.ZERO;
    }
}
