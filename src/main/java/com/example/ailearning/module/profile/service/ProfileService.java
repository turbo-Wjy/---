package com.example.ailearning.module.profile.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.crypto.AesGcmCryptoService;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.ai.entity.AiAgent;
import com.example.ailearning.module.ai.mapper.AiAgentMapper;
import com.example.ailearning.module.profile.dto.ProfileConfirmRequest;
import com.example.ailearning.module.profile.dto.ProfileDimensionDraft;
import com.example.ailearning.module.profile.dto.ProfileExtractRequest;
import com.example.ailearning.module.profile.dto.ProfileSessionCreateRequest;
import com.example.ailearning.module.profile.dto.ProfileSessionMessageRequest;
import com.example.ailearning.module.profile.entity.ProfileDimensionValue;
import com.example.ailearning.module.profile.entity.ProfileSession;
import com.example.ailearning.module.profile.entity.ProfileSessionMessage;
import com.example.ailearning.module.profile.entity.ProfileUpdateLog;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.ProfileDimensionValueMapper;
import com.example.ailearning.module.profile.mapper.ProfileSessionMapper;
import com.example.ailearning.module.profile.mapper.ProfileSessionMessageMapper;
import com.example.ailearning.module.profile.mapper.ProfileUpdateLogMapper;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.profile.vo.LearningProfileVO;
import com.example.ailearning.module.profile.vo.ProfileDimensionVO;
import com.example.ailearning.module.profile.vo.ProfileMessageVO;
import com.example.ailearning.module.profile.vo.ProfileSessionVO;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.service.StudentContextService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ProfileService {
    private static final String PROFILE_AGENT_CODE = "profile_builder_agent";

    private final StudentContextService studentContextService;
    private final AiAgentMapper aiAgentMapper;
    private final ProfileSessionMapper sessionMapper;
    private final ProfileSessionMessageMapper messageMapper;
    private final StudentProfileMapper profileMapper;
    private final ProfileDimensionValueMapper dimensionMapper;
    private final ProfileUpdateLogMapper updateLogMapper;
    private final AesGcmCryptoService cryptoService;
    private final ObjectMapper objectMapper;

    public ProfileService(
            StudentContextService studentContextService,
            AiAgentMapper aiAgentMapper,
            ProfileSessionMapper sessionMapper,
            ProfileSessionMessageMapper messageMapper,
            StudentProfileMapper profileMapper,
            ProfileDimensionValueMapper dimensionMapper,
            ProfileUpdateLogMapper updateLogMapper,
            AesGcmCryptoService cryptoService,
            ObjectMapper objectMapper
    ) {
        this.studentContextService = studentContextService;
        this.aiAgentMapper = aiAgentMapper;
        this.sessionMapper = sessionMapper;
        this.messageMapper = messageMapper;
        this.profileMapper = profileMapper;
        this.dimensionMapper = dimensionMapper;
        this.updateLogMapper = updateLogMapper;
        this.cryptoService = cryptoService;
        this.objectMapper = objectMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProfileSessionVO createSession(ProfileSessionCreateRequest request) {
        Student student = studentContextService.currentStudentRequired();
        ProfileSession session = new ProfileSession();
        session.setStudentId(student.getId());
        session.setAgentId(profileBuilderAgentId());
        session.setSessionTitle(request.getSessionTitle() == null || request.getSessionTitle().isBlank() ? "学习画像构建" : request.getSessionTitle());
        session.setConfirmStatus("draft");
        session.setStatus("active");
        session.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        sessionMapper.insert(session);
        return toSessionVO(session, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProfileSessionVO addMessage(Long sessionId, ProfileSessionMessageRequest request) {
        ProfileSession session = getOwnedSession(sessionId);
        insertMessage(session.getId(), "student", request.getContent(), null);
        insertMessage(session.getId(), "assistant", assistantReply(), Map.of("mode", "placeholder"));
        return toSessionVO(sessionMapper.selectById(session.getId()), true);
    }

    public ProfileSessionVO getSession(Long sessionId) {
        return toSessionVO(getOwnedSession(sessionId), true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProfileSessionVO extract(Long sessionId, ProfileExtractRequest request) {
        ProfileSession session = getOwnedSession(sessionId);
        List<ProfileDimensionDraft> dimensions = request.getDimensions();
        if (dimensions == null || dimensions.isEmpty()) {
            dimensions = defaultDimensions(session.getId());
        }
        String summary = request.getProfileSummary();
        if (summary == null || summary.isBlank()) {
            summary = defaultSummary(dimensions);
        }
        BigDecimal confidence = request.getConfidenceScore() == null ? BigDecimal.valueOf(0.82) : request.getConfidenceScore();
        session.setDraftProfileJson(toJson(Map.of("summary", summary)));
        session.setExtractedDimensionsJson(toJson(dimensions));
        session.setConfidenceScore(confidence);
        session.setConfirmStatus("extracted");
        sessionMapper.updateById(session);
        return toSessionVO(session, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningProfileVO confirm(ProfileConfirmRequest request) {
        ProfileSession session = getOwnedSession(request.getSessionId());
        if (!"extracted".equals(session.getConfirmStatus()) || session.getExtractedDimensionsJson() == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "请先抽取画像草稿后再确认");
        }
        List<ProfileDimensionDraft> dimensions = parseDimensions(session.getExtractedDimensionsJson());
        String summary = draftSummary(session.getDraftProfileJson());
        Long studentId = session.getStudentId();
        Integer nextVersion = nextProfileVersion(studentId);

        StudentProfile profile = new StudentProfile();
        profile.setStudentId(studentId);
        profile.setProfileVersion(nextVersion);
        AesGcmCryptoService.EncryptedValue encryptedSummary = cryptoService.encrypt(summary);
        profile.setProfileSummaryEncrypted(encryptedSummary.cipherText());
        profile.setProfileSummaryIv(encryptedSummary.iv());
        profile.setCompletenessScore(completeness(dimensions));
        profile.setLastGeneratedAt(LocalDateTime.now());
        profile.setStatus("active");
        profile.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        profileMapper.insert(profile);

        for (ProfileDimensionDraft dimension : dimensions) {
            ProfileDimensionValue value = new ProfileDimensionValue();
            value.setProfileId(profile.getId());
            value.setDimensionCode(dimension.getCode());
            value.setDimensionName(dimension.getName());
            AesGcmCryptoService.EncryptedValue encryptedValue = cryptoService.encrypt(dimension.getValue());
            value.setDimensionValueEncrypted(encryptedValue.cipherText());
            value.setDimensionValueIv(encryptedValue.iv());
            value.setConfidenceScore(dimension.getConfidence());
            value.setSourceType(dimension.getSource());
            value.setStatus("active");
            value.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
            dimensionMapper.insert(value);
        }

        session.setConfirmStatus("confirmed");
        session.setConfirmedProfileId(profile.getId());
        session.setConfirmedAt(LocalDateTime.now());
        sessionMapper.updateById(session);

        ProfileUpdateLog log = new ProfileUpdateLog();
        log.setStudentId(studentId);
        log.setSourceType("profile_session");
        log.setSourceId(session.getId());
        log.setAfterSnapshotJson(toJson(Map.of("profileId", profile.getId(), "version", profile.getProfileVersion(), "dimensionCount", dimensions.size())));
        log.setUpdatedReason("学生确认对话式画像草稿");
        log.setStatus("active");
        log.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        updateLogMapper.insert(log);

        return toLearningProfileVO(profile);
    }

    public LearningProfileVO myProfile() {
        Long studentId = studentContextService.currentStudentIdRequired();
        StudentProfile profile = latestProfile(studentId);
        return toLearningProfileVO(profile);
    }

    public List<LearningProfileVO> myVersions() {
        Long studentId = studentContextService.currentStudentIdRequired();
        return profileMapper.selectList(new LambdaQueryWrapper<StudentProfile>()
                        .eq(StudentProfile::getStudentId, studentId)
                        .isNull(StudentProfile::getDeletedAt)
                        .orderByDesc(StudentProfile::getProfileVersion))
                .stream().map(this::toLearningProfileVO).toList();
    }

    public List<ProfileSessionVO> myEvidence() {
        Long studentId = studentContextService.currentStudentIdRequired();
        return sessionMapper.selectList(new LambdaQueryWrapper<ProfileSession>()
                        .eq(ProfileSession::getStudentId, studentId)
                        .isNull(ProfileSession::getDeletedAt)
                        .orderByDesc(ProfileSession::getCreatedAt))
                .stream().map(session -> toSessionVO(session, false)).toList();
    }

    public LearningProfileVO studentProfile(Long studentId) {
        studentContextService.checkCanViewStudent(studentId);
        return toLearningProfileVO(latestProfile(studentId));
    }

    private ProfileSession getOwnedSession(Long sessionId) {
        Long studentId = studentContextService.currentStudentIdRequired();
        ProfileSession session = sessionMapper.selectById(sessionId);
        if (session == null || session.getDeletedAt() != null || !studentId.equals(session.getStudentId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "画像会话不存在");
        }
        return session;
    }

    private void insertMessage(Long sessionId, String role, String content, Object tokenUsage) {
        AesGcmCryptoService.EncryptedValue encrypted = cryptoService.encrypt(content);
        ProfileSessionMessage message = new ProfileSessionMessage();
        message.setSessionId(sessionId);
        message.setMessageRole(role);
        message.setMessageContentEncrypted(encrypted.cipherText());
        message.setMessageContentIv(encrypted.iv());
        message.setTokenUsageJson(toJson(tokenUsage));
        message.setStatus("active");
        message.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        messageMapper.insert(message);
    }

    private ProfileSessionVO toSessionVO(ProfileSession session, boolean includeMessages) {
        ProfileSessionVO vo = new ProfileSessionVO();
        vo.setId(session.getId());
        vo.setStudentId(session.getStudentId());
        vo.setSessionTitle(session.getSessionTitle());
        vo.setConfirmStatus(session.getConfirmStatus());
        vo.setConfidenceScore(session.getConfidenceScore());
        vo.setConfirmedProfileId(session.getConfirmedProfileId());
        vo.setDraftProfile(draftSummary(session.getDraftProfileJson()));
        vo.setDimensions(parseDimensionVOs(session.getExtractedDimensionsJson()));
        vo.setCreatedAt(session.getCreatedAt());
        if (includeMessages) {
            vo.setMessages(listMessages(session.getId()));
        }
        return vo;
    }

    private LearningProfileVO toLearningProfileVO(StudentProfile profile) {
        LearningProfileVO vo = new LearningProfileVO();
        vo.setId(profile.getId());
        vo.setStudentId(profile.getStudentId());
        vo.setProfileVersion(profile.getProfileVersion());
        vo.setProfileSummary(cryptoService.decrypt(profile.getProfileSummaryEncrypted(), profile.getProfileSummaryIv()));
        vo.setCompletenessScore(profile.getCompletenessScore());
        vo.setLastGeneratedAt(profile.getLastGeneratedAt());
        vo.setDimensions(dimensionMapper.selectList(new LambdaQueryWrapper<ProfileDimensionValue>()
                        .eq(ProfileDimensionValue::getProfileId, profile.getId())
                        .isNull(ProfileDimensionValue::getDeletedAt)
                        .orderByAsc(ProfileDimensionValue::getId))
                .stream().map(this::toDimensionVO).toList());
        return vo;
    }

    private ProfileDimensionVO toDimensionVO(ProfileDimensionValue value) {
        ProfileDimensionVO vo = new ProfileDimensionVO();
        vo.setCode(value.getDimensionCode());
        vo.setName(value.getDimensionName());
        vo.setValue(cryptoService.decrypt(value.getDimensionValueEncrypted(), value.getDimensionValueIv()));
        vo.setConfidence(value.getConfidenceScore());
        vo.setSource(value.getSourceType());
        return vo;
    }

    private List<ProfileMessageVO> listMessages(Long sessionId) {
        return messageMapper.selectList(new LambdaQueryWrapper<ProfileSessionMessage>()
                        .eq(ProfileSessionMessage::getSessionId, sessionId)
                        .isNull(ProfileSessionMessage::getDeletedAt)
                        .orderByAsc(ProfileSessionMessage::getCreatedAt)
                        .orderByAsc(ProfileSessionMessage::getId))
                .stream().map(message -> {
                    ProfileMessageVO vo = new ProfileMessageVO();
                    vo.setId(message.getId());
                    vo.setRole(message.getMessageRole());
                    vo.setContent(cryptoService.decrypt(message.getMessageContentEncrypted(), message.getMessageContentIv()));
                    vo.setCreatedAt(message.getCreatedAt());
                    return vo;
                }).toList();
    }

    private StudentProfile latestProfile(Long studentId) {
        StudentProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, studentId)
                .isNull(StudentProfile::getDeletedAt)
                .orderByDesc(StudentProfile::getProfileVersion)
                .last("LIMIT 1"));
        if (profile == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生画像不存在，请先完成画像构建");
        }
        return profile;
    }

    private Integer nextProfileVersion(Long studentId) {
        StudentProfile latest = profileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, studentId)
                .isNull(StudentProfile::getDeletedAt)
                .orderByDesc(StudentProfile::getProfileVersion)
                .last("LIMIT 1"));
        return latest == null ? 1 : latest.getProfileVersion() + 1;
    }

    private Long profileBuilderAgentId() {
        AiAgent agent = aiAgentMapper.selectOne(new LambdaQueryWrapper<AiAgent>()
                .eq(AiAgent::getCode, PROFILE_AGENT_CODE)
                .isNull(AiAgent::getDeletedAt)
                .last("LIMIT 1"));
        return agent == null ? null : agent.getId();
    }

    private String assistantReply() {
        return "已记录你的学习信息。你可以继续补充专业方向、目标岗位、近期课程、薄弱知识点、喜欢的资源形式或项目经历。";
    }

    private List<ProfileDimensionDraft> defaultDimensions(Long sessionId) {
        String joined = messageMapper.selectList(new LambdaQueryWrapper<ProfileSessionMessage>()
                        .eq(ProfileSessionMessage::getSessionId, sessionId)
                        .eq(ProfileSessionMessage::getMessageRole, "student")
                        .isNull(ProfileSessionMessage::getDeletedAt)
                        .orderByAsc(ProfileSessionMessage::getId))
                .stream()
                .map(message -> cryptoService.decrypt(message.getMessageContentEncrypted(), message.getMessageContentIv()))
                .reduce("", (left, right) -> left + " " + right);
        List<ProfileDimensionDraft> dimensions = new ArrayList<>();
        dimensions.add(dimension("knowledge_foundation", "知识基础", valueOrDefault(joined, "已完成基础画像对话，知识基础待结合课程记录继续校准。"), "chat", 0.78));
        dimensions.add(dimension("learning_goal", "学习目标", "围绕目标岗位、课程学习和竞赛任务持续提升。", "chat", 0.82));
        dimensions.add(dimension("cognitive_style", "认知风格", "偏好通过案例、图谱和任务拆解理解知识。", "chat", 0.76));
        dimensions.add(dimension("knowledge_gap", "知识短板", "需要结合答题记录、错题和岗位能力要求进一步定位。", "chat", 0.72));
        dimensions.add(dimension("error_prone_points", "易错点", "暂未形成稳定错题模式，后续随练习记录更新。", "chat", 0.70));
        dimensions.add(dimension("resource_preference", "资源偏好", "推荐文档、PPT、题库、实操案例组合学习。", "chat", 0.80));
        dimensions.add(dimension("practice_ability", "实践能力", "实践能力待结合项目实训和竞赛成果动态评估。", "chat", 0.74));
        dimensions.add(dimension("learning_progress", "学习进度", "已启动个性化学习画像构建。", "chat", 0.86));
        return dimensions;
    }

    private ProfileDimensionDraft dimension(String code, String name, String value, String source, double confidence) {
        ProfileDimensionDraft draft = new ProfileDimensionDraft();
        draft.setCode(code);
        draft.setName(name);
        draft.setValue(value);
        draft.setSource(source);
        draft.setConfidence(BigDecimal.valueOf(confidence));
        return draft;
    }

    private String defaultSummary(List<ProfileDimensionDraft> dimensions) {
        return "系统已基于对话初步生成学习画像，包含 " + dimensions.size() + " 个维度，可随课程学习、答题、竞赛、证书和项目数据持续更新。";
    }

    private BigDecimal completeness(List<ProfileDimensionDraft> dimensions) {
        long filled = dimensions.stream().filter(d -> d.getValue() != null && !d.getValue().isBlank()).count();
        return BigDecimal.valueOf(Math.min(100, filled * 100 / 8));
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private List<ProfileDimensionDraft> parseDimensions(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<ProfileDimensionDraft>>() {});
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "画像维度解析失败");
        }
    }

    private List<ProfileDimensionVO> parseDimensionVOs(String json) {
        return parseDimensions(json).stream()
                .sorted(Comparator.comparing(ProfileDimensionDraft::getCode))
                .map(draft -> {
                    ProfileDimensionVO vo = new ProfileDimensionVO();
                    vo.setCode(draft.getCode());
                    vo.setName(draft.getName());
                    vo.setValue(draft.getValue());
                    vo.setConfidence(draft.getConfidence());
                    vo.setSource(draft.getSource());
                    return vo;
                }).toList();
    }

    private String draftSummary(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            Object summary = map.get("summary");
            return summary == null ? null : summary.toString();
        } catch (JsonProcessingException e) {
            return null;
        }
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
