package com.example.ailearning.module.employment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.achievement.dto.ReviewRequest;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.employment.dto.JobPostRequest;
import com.example.ailearning.module.employment.entity.EnterpriseMentor;
import com.example.ailearning.module.employment.entity.JobPost;
import com.example.ailearning.module.employment.mapper.EnterpriseMapper;
import com.example.ailearning.module.employment.mapper.EnterpriseMentorMapper;
import com.example.ailearning.module.employment.mapper.JobPostMapper;
import com.example.ailearning.module.employment.vo.JobPostVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobPostService {
    private final JobPostMapper jobPostMapper;
    private final EnterpriseMentorMapper mentorMapper;
    private final EnterpriseMapper enterpriseMapper;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public JobPostService(
            JobPostMapper jobPostMapper,
            EnterpriseMentorMapper mentorMapper,
            EnterpriseMapper enterpriseMapper,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.jobPostMapper = jobPostMapper;
        this.mentorMapper = mentorMapper;
        this.enterpriseMapper = enterpriseMapper;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    public PageResult<JobPostVO> page(PageQuery query, Long majorId, String reviewStatus) {
        Page<JobPost> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<JobPost> wrapper = new LambdaQueryWrapper<JobPost>()
                .isNull(JobPost::getDeletedAt)
                .orderByDesc(JobPost::getCreatedAt);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(JobPost::getTitle, query.getKeyword());
        }
        if (majorId != null) {
            wrapper.eq(JobPost::getMajorId, majorId);
        }
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(JobPost::getReviewStatus, reviewStatus);
        }
        Page<JobPost> result = jobPostMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toVO).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    public JobPostVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public JobPostVO create(JobPostRequest request) {
        EnterpriseMentor mentor = currentMentorRequired();
        if (enterpriseMapper.selectById(mentor.getEnterpriseId()) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "企业不存在");
        }
        JobPost post = new JobPost();
        post.setEnterpriseId(mentor.getEnterpriseId());
        post.setMentorId(mentor.getId());
        post.setMajorId(request.getMajorId());
        post.setTitle(request.getTitle());
        post.setRequirements(request.getRequirements());
        post.setSalaryRange(request.getSalaryRange());
        post.setLocation(request.getLocation());
        post.setAbilityTagsJson(toJson(request.getAbilityTags() == null ? List.of() : request.getAbilityTags()));
        boolean submit = !Boolean.FALSE.equals(request.getSubmitReview());
        post.setReviewStatus(submit ? "pending" : "draft");
        post.setSubmittedAt(submit ? LocalDateTime.now() : null);
        post.setStatus(submit ? "pending" : "draft");
        post.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        jobPostMapper.insert(post);
        auditService.operation("job_ability", "create_job_post", "job_post", post.getId(), "success", "企业导师发布岗位");
        return toVO(post);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobPostVO review(Long id, ReviewRequest request) {
        JobPost post = getEntity(id);
        String result = request.getReviewResult();
        post.setReviewStatus("approved".equals(result) ? "published" : "rejected");
        post.setStatus("approved".equals(result) ? "published" : "rejected");
        post.setApprovedAt("approved".equals(result) ? LocalDateTime.now() : null);
        jobPostMapper.updateById(post);
        auditService.review("job_post", post.getId(), "major_review", result, request.getReviewComment());
        return toVO(post);
    }

    public JobPost getEntity(Long id) {
        JobPost post = jobPostMapper.selectById(id);
        if (post == null || post.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "岗位不存在");
        }
        return post;
    }

    EnterpriseMentor currentMentorRequired() {
        EnterpriseMentor mentor = mentorMapper.selectOne(new LambdaQueryWrapper<EnterpriseMentor>()
                .eq(EnterpriseMentor::getUserId, CurrentUserHolder.getRequired().getUserId())
                .isNull(EnterpriseMentor::getDeletedAt)
                .last("LIMIT 1"));
        if (mentor == null) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "当前账号未绑定企业导师信息");
        }
        return mentor;
    }

    JobPostVO toVO(JobPost post) {
        JobPostVO vo = new JobPostVO();
        vo.setId(post.getId());
        vo.setEnterpriseId(post.getEnterpriseId());
        vo.setMentorId(post.getMentorId());
        vo.setMajorId(post.getMajorId());
        vo.setTitle(post.getTitle());
        vo.setRequirements(post.getRequirements());
        vo.setSalaryRange(post.getSalaryRange());
        vo.setLocation(post.getLocation());
        vo.setAbilityTags(parseList(post.getAbilityTagsJson()));
        vo.setReviewStatus(post.getReviewStatus());
        vo.setSubmittedAt(post.getSubmittedAt());
        vo.setApprovedAt(post.getApprovedAt());
        vo.setStatus(post.getStatus());
        vo.setCreatedAt(post.getCreatedAt());
        return vo;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "JSON 字段格式不正确");
        }
    }

    private List<String> parseList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
