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
import com.example.ailearning.module.employment.dto.EnterpriseReviewRequest;
import com.example.ailearning.module.employment.dto.JobApplicationSubmitRequest;
import com.example.ailearning.module.employment.entity.EnterpriseMentor;
import com.example.ailearning.module.employment.entity.JobApplication;
import com.example.ailearning.module.employment.entity.JobPost;
import com.example.ailearning.module.employment.entity.Resume;
import com.example.ailearning.module.employment.mapper.JobApplicationMapper;
import com.example.ailearning.module.employment.vo.JobApplicationVO;
import com.example.ailearning.module.student.service.StudentContextService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JobApplicationService {
    private final JobApplicationMapper applicationMapper;
    private final JobPostService jobPostService;
    private final ResumeService resumeService;
    private final StudentContextService studentContextService;
    private final AuditService auditService;

    public JobApplicationService(
            JobApplicationMapper applicationMapper,
            JobPostService jobPostService,
            ResumeService resumeService,
            StudentContextService studentContextService,
            AuditService auditService
    ) {
        this.applicationMapper = applicationMapper;
        this.jobPostService = jobPostService;
        this.resumeService = resumeService;
        this.studentContextService = studentContextService;
        this.auditService = auditService;
    }

    public PageResult<JobApplicationVO> page(PageQuery query, Long studentId, Long jobId, String applicationStatus) {
        Page<JobApplication> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<JobApplication>()
                .isNull(JobApplication::getDeletedAt)
                .orderByDesc(JobApplication::getCreatedAt);
        if (studentId != null) {
            wrapper.eq(JobApplication::getStudentId, studentId);
        }
        if (jobId != null) {
            wrapper.eq(JobApplication::getJobId, jobId);
        }
        if (applicationStatus != null && !applicationStatus.isBlank()) {
            wrapper.eq(JobApplication::getApplicationStatus, applicationStatus);
        }
        Page<JobApplication> result = applicationMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toVO).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplicationVO submit(JobApplicationSubmitRequest request) {
        Long studentId = studentContextService.currentStudentIdRequired();
        JobPost post = jobPostService.getEntity(request.getJobId());
        if (!"published".equals(post.getReviewStatus()) && !"published".equals(post.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "岗位未发布，不能投递");
        }
        Resume resume = resumeService.ownedResume(request.getResumeId());
        if (!Boolean.TRUE.equals(resume.getStudentConfirmed())) {
            throw new BusinessException(ErrorCode.CONFLICT, "请先确认简历后再投递");
        }
        if (!studentId.equals(resume.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能投递自己的简历");
        }
        boolean exists = applicationMapper.exists(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getJobId, request.getJobId())
                .eq(JobApplication::getStudentId, studentId)
                .isNull(JobApplication::getDeletedAt));
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT, "该岗位已投递");
        }
        JobApplication application = new JobApplication();
        application.setJobId(request.getJobId());
        application.setResumeId(request.getResumeId());
        application.setStudentId(studentId);
        application.setApplicationStatus("pending_teacher_review");
        application.setSubmittedAt(LocalDateTime.now());
        application.setStatus("pending");
        application.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        applicationMapper.insert(application);
        auditService.operation("job_ability", "submit_job_application", "job_application", application.getId(), "success", "学生提交岗位投递");
        return toVO(application);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplicationVO teacherReview(Long id, ReviewRequest request) {
        JobApplication application = getEntity(id);
        studentContextService.checkCanViewStudent(application.getStudentId());
        if ("approved".equals(request.getReviewResult())) {
            application.setApplicationStatus("pending_enterprise_review");
            application.setStatus("pending");
        } else {
            application.setApplicationStatus("teacher_rejected");
            application.setStatus("rejected");
        }
        applicationMapper.updateById(application);
        auditService.review("job_application", application.getId(), "teacher_group_review", request.getReviewResult(), request.getReviewComment());
        return toVO(application);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplicationVO enterpriseReview(Long id, EnterpriseReviewRequest request) {
        JobApplication application = getEntity(id);
        checkCurrentMentorOwnsJob(application);
        application.setApplicationStatus(request.getReviewResult());
        application.setEnterpriseFeedback(request.getFeedback());
        application.setStatus("rejected".equals(request.getReviewResult()) ? "rejected" : "approved");
        applicationMapper.updateById(application);
        auditService.review("job_application", application.getId(), "enterprise_review", request.getReviewResult(), request.getFeedback());
        return toVO(application);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplicationVO recommend(Long id, EnterpriseReviewRequest request) {
        JobApplication application = getEntity(id);
        checkCurrentMentorOwnsJob(application);
        application.setApplicationStatus("recommended");
        application.setEnterpriseFeedback(request.getFeedback());
        application.setStatus("approved");
        applicationMapper.updateById(application);
        auditService.review("job_application", application.getId(), "enterprise_review", "recommended", request.getFeedback());
        auditService.operation("job_ability", "recommend_job_application", "job_application", application.getId(), "success", "企业导师推荐简历给企业");
        return toVO(application);
    }

    private void checkCurrentMentorOwnsJob(JobApplication application) {
        JobPost post = jobPostService.getEntity(application.getJobId());
        EnterpriseMentor mentor = jobPostService.currentMentorRequired();
        if (!mentor.getId().equals(post.getMentorId())) {
            throw new BusinessException(ErrorCode.DATA_SCOPE_FORBIDDEN, "只能审核自己发布岗位的投递");
        }
    }

    private JobApplication getEntity(Long id) {
        JobApplication application = applicationMapper.selectById(id);
        if (application == null || application.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "岗位投递不存在");
        }
        return application;
    }

    private JobApplicationVO toVO(JobApplication application) {
        JobApplicationVO vo = new JobApplicationVO();
        vo.setId(application.getId());
        vo.setJobId(application.getJobId());
        vo.setResumeId(application.getResumeId());
        vo.setStudentId(application.getStudentId());
        vo.setApplicationStatus(application.getApplicationStatus());
        vo.setSubmittedAt(application.getSubmittedAt());
        vo.setEnterpriseFeedback(application.getEnterpriseFeedback());
        vo.setStatus(application.getStatus());
        vo.setCreatedAt(application.getCreatedAt());
        return vo;
    }
}
