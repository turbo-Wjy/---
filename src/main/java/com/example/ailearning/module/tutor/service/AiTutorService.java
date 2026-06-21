package com.example.ailearning.module.tutor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.crypto.AesGcmCryptoService;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.course.entity.CourseKnowledgePoint;
import com.example.ailearning.module.course.entity.WrongQuestion;
import com.example.ailearning.module.course.mapper.CourseKnowledgePointMapper;
import com.example.ailearning.module.course.mapper.WrongQuestionMapper;
import com.example.ailearning.module.evaluation.entity.LearningEvaluation;
import com.example.ailearning.module.evaluation.mapper.LearningEvaluationMapper;
import com.example.ailearning.module.learning.entity.ResourceRecommendation;
import com.example.ailearning.module.learning.mapper.ResourceRecommendationMapper;
import com.example.ailearning.module.profile.entity.ProfileDimensionValue;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.ProfileDimensionValueMapper;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.student.service.StudentContextService;
import com.example.ailearning.module.tutor.dto.AiTutorChatRequest;
import com.example.ailearning.module.tutor.dto.AiTutorFeedbackRequest;
import com.example.ailearning.module.tutor.entity.AiTutoringSession;
import com.example.ailearning.module.tutor.mapper.AiTutoringSessionMapper;
import com.example.ailearning.module.tutor.vo.AiTutoringSessionVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiTutorService {
    private final StudentContextService studentContextService;
    private final AiTutoringSessionMapper tutoringSessionMapper;
    private final CourseKnowledgePointMapper knowledgePointMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final StudentProfileMapper profileMapper;
    private final ProfileDimensionValueMapper dimensionMapper;
    private final LearningEvaluationMapper evaluationMapper;
    private final ResourceRecommendationMapper recommendationMapper;
    private final AesGcmCryptoService cryptoService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public AiTutorService(
            StudentContextService studentContextService,
            AiTutoringSessionMapper tutoringSessionMapper,
            CourseKnowledgePointMapper knowledgePointMapper,
            WrongQuestionMapper wrongQuestionMapper,
            StudentProfileMapper profileMapper,
            ProfileDimensionValueMapper dimensionMapper,
            LearningEvaluationMapper evaluationMapper,
            ResourceRecommendationMapper recommendationMapper,
            AesGcmCryptoService cryptoService,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.studentContextService = studentContextService;
        this.tutoringSessionMapper = tutoringSessionMapper;
        this.knowledgePointMapper = knowledgePointMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.profileMapper = profileMapper;
        this.dimensionMapper = dimensionMapper;
        this.evaluationMapper = evaluationMapper;
        this.recommendationMapper = recommendationMapper;
        this.cryptoService = cryptoService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public AiTutoringSessionVO chat(AiTutorChatRequest request) {
        Long studentId = studentContextService.currentStudentIdRequired();
        CourseKnowledgePoint knowledgePoint = findKnowledgePoint(request.getKnowledgePointId());
        TutorContext context = buildContext(studentId, knowledgePoint);
        String answerText = buildAnswerText(request.getQuestion().trim(), knowledgePoint, context);
        Map<String, Object> answerAssets = buildAnswerAssets(request, knowledgePoint, context);

        AesGcmCryptoService.EncryptedValue encryptedQuestion = cryptoService.encrypt(request.getQuestion().trim());
        AesGcmCryptoService.EncryptedValue encryptedAnswer = cryptoService.encrypt(answerText);

        AiTutoringSession session = new AiTutoringSession();
        session.setStudentId(studentId);
        session.setKnowledgePointId(request.getKnowledgePointId());
        session.setQuestionEncrypted(encryptedQuestion.cipherText());
        session.setQuestionIv(encryptedQuestion.iv());
        session.setAnswerTextEncrypted(encryptedAnswer.cipherText());
        session.setAnswerTextIv(encryptedAnswer.iv());
        session.setAnswerAssetsJson(toJson(answerAssets));
        session.setStatus("active");
        session.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        tutoringSessionMapper.insert(session);

        auditService.operation("ai_learning_center", "ai_tutor_chat", "ai_tutoring_session", session.getId(), "success", "学生使用智能辅导生成答疑内容");
        return toVO(session, knowledgePoint);
    }

    public List<AiTutoringSessionVO> mySessions(Long knowledgePointId) {
        Long studentId = studentContextService.currentStudentIdRequired();
        LambdaQueryWrapper<AiTutoringSession> wrapper = new LambdaQueryWrapper<AiTutoringSession>()
                .eq(AiTutoringSession::getStudentId, studentId)
                .isNull(AiTutoringSession::getDeletedAt)
                .orderByDesc(AiTutoringSession::getCreatedAt);
        if (knowledgePointId != null) {
            wrapper.eq(AiTutoringSession::getKnowledgePointId, knowledgePointId);
        }
        return tutoringSessionMapper.selectList(wrapper).stream()
                .map(session -> toVO(session, findKnowledgePoint(session.getKnowledgePointId())))
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public AiTutoringSessionVO feedback(Long sessionId, AiTutorFeedbackRequest request) {
        Long studentId = studentContextService.currentStudentIdRequired();
        AiTutoringSession session = tutoringSessionMapper.selectById(sessionId);
        if (session == null || session.getDeletedAt() != null || !studentId.equals(session.getStudentId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "智能辅导记录不存在");
        }
        session.setFeedbackScore(request.getFeedbackScore());
        tutoringSessionMapper.updateById(session);
        auditService.operation("ai_learning_center", "ai_tutor_feedback", "ai_tutoring_session", session.getId(), "success", "学生反馈智能辅导效果");
        return toVO(session, findKnowledgePoint(session.getKnowledgePointId()));
    }

    private CourseKnowledgePoint findKnowledgePoint(Long knowledgePointId) {
        if (knowledgePointId == null) {
            return null;
        }
        CourseKnowledgePoint point = knowledgePointMapper.selectById(knowledgePointId);
        if (point == null || point.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识点不存在");
        }
        return point;
    }

    private TutorContext buildContext(Long studentId, CourseKnowledgePoint knowledgePoint) {
        TutorContext context = new TutorContext();
        StudentProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, studentId)
                .isNull(StudentProfile::getDeletedAt)
                .orderByDesc(StudentProfile::getProfileVersion)
                .last("LIMIT 1"));
        if (profile != null) {
            context.profileSummary = safeDecrypt(profile.getProfileSummaryEncrypted(), profile.getProfileSummaryIv());
            context.profileDimensions = dimensionMapper.selectList(new LambdaQueryWrapper<ProfileDimensionValue>()
                            .eq(ProfileDimensionValue::getProfileId, profile.getId())
                            .isNull(ProfileDimensionValue::getDeletedAt)
                            .orderByAsc(ProfileDimensionValue::getId))
                    .stream()
                    .map(value -> value.getDimensionName() + "：" + safeDecrypt(value.getDimensionValueEncrypted(), value.getDimensionValueIv()))
                    .toList();
        }
        context.latestEvaluation = evaluationMapper.selectOne(new LambdaQueryWrapper<LearningEvaluation>()
                .eq(LearningEvaluation::getStudentId, studentId)
                .isNull(LearningEvaluation::getDeletedAt)
                .orderByDesc(LearningEvaluation::getCreatedAt)
                .last("LIMIT 1"));
        LambdaQueryWrapper<WrongQuestion> wrongWrapper = new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getStudentId, studentId)
                .isNull(WrongQuestion::getDeletedAt)
                .orderByDesc(WrongQuestion::getCreatedAt)
                .last("LIMIT 5");
        if (knowledgePoint != null) {
            wrongWrapper.eq(WrongQuestion::getKnowledgePointId, knowledgePoint.getId());
        }
        context.recentWrongQuestions = wrongQuestionMapper.selectList(wrongWrapper);
        context.unreadRecommendations = recommendationMapper.selectList(new LambdaQueryWrapper<ResourceRecommendation>()
                .eq(ResourceRecommendation::getStudentId, studentId)
                .eq(ResourceRecommendation::getViewStatus, "unread")
                .isNull(ResourceRecommendation::getDeletedAt)
                .orderByDesc(ResourceRecommendation::getCreatedAt)
                .last("LIMIT 5"));
        return context;
    }

    private String buildAnswerText(String question, CourseKnowledgePoint knowledgePoint, TutorContext context) {
        String pointName = knowledgePoint == null ? "当前问题" : "知识点「" + knowledgePoint.getName() + "」";
        StringBuilder answer = new StringBuilder();
        answer.append("我先按“概念澄清 -> 关键步骤 -> 易错提醒 -> 练习建议”的顺序帮你拆开。\n\n");
        answer.append("1. 概念澄清：").append(pointName).append("可以先从问题中的关键词入手：").append(shortText(question, 60)).append("。\n");
        answer.append("2. 关键步骤：先确认输入条件，再拆分核心概念，最后用一个小案例验证自己的理解。\n");
        if (!context.recentWrongQuestions.isEmpty()) {
            answer.append("3. 易错提醒：你在相关练习中已有 ").append(context.recentWrongQuestions.size()).append(" 条错题记录，建议重点复盘题目条件、概念边界和解题步骤。\n");
        } else {
            answer.append("3. 易错提醒：目前相关错题较少，但仍建议检查概念定义、适用场景和结果解释是否一致。\n");
        }
        if (context.latestEvaluation != null) {
            answer.append("4. 学习状态参考：最近一次评估提示：").append(context.latestEvaluation.getEvaluationSummary()).append("\n");
        }
        answer.append("5. 下一步建议：完成 3 道基础题、1 道应用题，再把你的解题过程发给我，我可以继续帮你定位卡点。");
        return answer.toString();
    }

    private Map<String, Object> buildAnswerAssets(AiTutorChatRequest request, CourseKnowledgePoint knowledgePoint, TutorContext context) {
        Map<String, Object> assets = new LinkedHashMap<>();
        assets.put("answerMode", request.getAnswerMode() == null || request.getAnswerMode().isBlank() ? "text_diagram_practice" : request.getAnswerMode());
        assets.put("knowledgePoint", knowledgePoint == null ? null : Map.of(
                "id", knowledgePoint.getId(),
                "name", knowledgePoint.getName(),
                "difficultyLevel", knowledgePoint.getDifficultyLevel()
        ));
        assets.put("diagramSteps", List.of(
                "定位问题关键词",
                "匹配课程知识点与画像短板",
                "拆分概念、步骤和易错点",
                "完成练习并回写学习记录"
        ));
        assets.put("videoScriptOutline", List.of(
                "开场：用一句话说明问题场景",
                "主体：用图解方式拆解概念和步骤",
                "演示：给出一个小案例或伪代码",
                "收尾：布置针对性练习并提醒错题复盘"
        ));
        assets.put("practiceSuggestions", practiceSuggestions(context));
        assets.put("evidence", evidence(context));
        return assets;
    }

    private List<String> practiceSuggestions(TutorContext context) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("先做 3 道基础概念题，确认定义和边界。");
        suggestions.add("再做 1 道场景应用题，把解题步骤写完整。");
        if (!context.recentWrongQuestions.isEmpty()) {
            suggestions.add("复盘最近 " + context.recentWrongQuestions.size() + " 道相关错题，标注错误原因。");
        }
        if (!context.unreadRecommendations.isEmpty()) {
            suggestions.add("查看 " + context.unreadRecommendations.size() + " 个未读推荐资源，优先选择与当前知识点相关的内容。");
        }
        return suggestions;
    }

    private Map<String, Object> evidence(TutorContext context) {
        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("profileSummary", context.profileSummary);
        evidence.put("profileDimensions", context.profileDimensions);
        evidence.put("latestEvaluationSummary", context.latestEvaluation == null ? null : context.latestEvaluation.getEvaluationSummary());
        evidence.put("wrongQuestionCount", context.recentWrongQuestions.size());
        evidence.put("unreadRecommendationCount", context.unreadRecommendations.size());
        return evidence;
    }

    private AiTutoringSessionVO toVO(AiTutoringSession session, CourseKnowledgePoint knowledgePoint) {
        AiTutoringSessionVO vo = new AiTutoringSessionVO();
        vo.setId(session.getId());
        vo.setStudentId(session.getStudentId());
        vo.setKnowledgePointId(session.getKnowledgePointId());
        vo.setKnowledgePointName(knowledgePoint == null ? null : knowledgePoint.getName());
        vo.setQuestion(safeDecrypt(session.getQuestionEncrypted(), session.getQuestionIv()));
        vo.setAnswerText(safeDecrypt(session.getAnswerTextEncrypted(), session.getAnswerTextIv()));
        vo.setAnswerAssets(parseMap(session.getAnswerAssetsJson()));
        vo.setFeedbackScore(session.getFeedbackScore());
        vo.setStatus(session.getStatus());
        vo.setCreatedAt(session.getCreatedAt());
        return vo;
    }

    private String shortText(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    private String safeDecrypt(String encrypted, String iv) {
        try {
            return cryptoService.decrypt(encrypted, iv);
        } catch (BusinessException e) {
            return "[历史加密内容暂不可解密]";
        }
    }

    private Map<String, Object> parseMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "JSON 字段格式不正确");
        }
    }

    private static class TutorContext {
        private String profileSummary;
        private List<String> profileDimensions = List.of();
        private LearningEvaluation latestEvaluation;
        private List<WrongQuestion> recentWrongQuestions = List.of();
        private List<ResourceRecommendation> unreadRecommendations = List.of();
    }
}
