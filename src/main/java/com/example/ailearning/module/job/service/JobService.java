package com.example.ailearning.module.job.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.crypto.AesGcmCryptoService;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.job.dto.JobApplicationRequest;
import com.example.ailearning.module.job.dto.JobApplicationReviewRequest;
import com.example.ailearning.module.job.dto.JobPostRequest;
import com.example.ailearning.module.job.dto.ResumeGenerateRequest;
import com.example.ailearning.module.job.entity.JobApplication;
import com.example.ailearning.module.job.entity.JobPost;
import com.example.ailearning.module.job.entity.Resume;
import com.example.ailearning.module.job.mapper.JobApplicationMapper;
import com.example.ailearning.module.job.mapper.JobPostMapper;
import com.example.ailearning.module.job.mapper.ResumeMapper;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.service.StudentContextService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {
    private final JobPostMapper jobPostMapper;
    private final ResumeMapper resumeMapper;
    private final JobApplicationMapper applicationMapper;
    private final StudentContextService studentContextService;
    private final AesGcmCryptoService cryptoService;

    public JobService(
            JobPostMapper jobPostMapper,
            ResumeMapper resumeMapper,
            JobApplicationMapper applicationMapper,
            StudentContextService studentContextService,
            AesGcmCryptoService cryptoService
    ) {
        this.jobPostMapper = jobPostMapper;
        this.resumeMapper = resumeMapper;
        this.applicationMapper = applicationMapper;
        this.studentContextService = studentContextService;
        this.cryptoService = cryptoService;
    }

    public List<JobPost> listJobPosts(Long majorId, String reviewStatus) {
        LambdaQueryWrapper<JobPost> wrapper = new LambdaQueryWrapper<JobPost>()
                .isNull(JobPost::getDeletedAt)
                .orderByDesc(JobPost::getCreatedAt);
        if (majorId != null) {
            wrapper.eq(JobPost::getMajorId, majorId);
        }
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(JobPost::getReviewStatus, reviewStatus);
        }
        return jobPostMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobPost createJobPost(JobPostRequest request) {
        Long userId = CurrentUserHolder.getRequired().getUserId();
        JobPost post = new JobPost();
        post.setEnterpriseId(request.getEnterpriseId());
        post.setMentorId(request.getMentorId());
        post.setMajorId(request.getMajorId());
        post.setTitle(request.getTitle());
        post.setRequirements(request.getRequirements());
        post.setSalaryRange(request.getSalaryRange());
        post.setLocation(request.getLocation());
        post.setAbilityTagsJson(request.getAbilityTags());
        post.setReviewStatus("pending");
        post.setSubmittedAt(LocalDateTime.now());
        post.setStatus("pending");
        post.setCreatedBy(userId);
        jobPostMapper.insert(post);
        return post;
    }

    @Transactional(rollbackFor = Exception.class)
    public Resume generateResume(ResumeGenerateRequest request) {
        Student student = studentContextService.currentStudentRequired();
        Long userId = CurrentUserHolder.getRequired().getUserId();
        String content = request.getResumeContent() == null || request.getResumeContent().isBlank()
                ? "基于学习画像、课程成果、竞赛证书与项目经历生成的简历草稿。"
                : request.getResumeContent();
        AesGcmCryptoService.EncryptedValue encrypted = cryptoService.encrypt(content);

        Resume resume = new Resume();
        resume.setStudentId(student.getId());
        resume.setTargetJobId(request.getTargetJobId());
        resume.setResumeContentEncrypted(encrypted.cipherText());
        resume.setResumeContentIv(encrypted.iv());
        resume.setResumeSummary(request.getResumeSummary() == null || request.getResumeSummary().isBlank() ? content : request.getResumeSummary());
        resume.setStudentConfirmed(Boolean.TRUE.equals(request.getConfirm()));
        resume.setConfirmedAt(Boolean.TRUE.equals(request.getConfirm()) ? LocalDateTime.now() : null);
        resume.setStatus(Boolean.TRUE.equals(request.getConfirm()) ? "confirmed" : "draft");
        resume.setCreatedBy(userId);
        resumeMapper.insert(resume);
        return resume;
    }

    public List<Resume> myResumes() {
        Student student = studentContextService.currentStudentRequired();
        return resumeMapper.selectList(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getStudentId, student.getId())
                .isNull(Resume::getDeletedAt)
                .orderByDesc(Resume::getCreatedAt));
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplication submitApplication(JobApplicationRequest request) {
        Student student = studentContextService.currentStudentRequired();
        Resume resume = resumeMapper.selectById(request.getResumeId());
        if (resume == null || resume.getDeletedAt() != null || !student.getId().equals(resume.getStudentId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "简历不存在");
        }
        JobApplication application = new JobApplication();
        application.setJobId(request.getJobId());
        application.setResumeId(request.getResumeId());
        application.setStudentId(student.getId());
        application.setApplicationStatus("pending_teacher_review");
        application.setSubmittedAt(LocalDateTime.now());
        application.setStatus("pending");
        application.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        applicationMapper.insert(application);
        return application;
    }

    public List<JobApplication> listApplications(String applicationStatus) {
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<JobApplication>()
                .isNull(JobApplication::getDeletedAt)
                .orderByDesc(JobApplication::getCreatedAt);
        if (applicationStatus != null && !applicationStatus.isBlank()) {
            wrapper.eq(JobApplication::getApplicationStatus, applicationStatus);
        }
        return applicationMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplication teacherReview(Long id, JobApplicationReviewRequest request) {
        JobApplication application = getApplication(id);
        application.setApplicationStatus("approved".equals(request.getResult()) ? "pending_enterprise_review" : "teacher_rejected");
        application.setEnterpriseFeedback(request.getFeedback());
        applicationMapper.updateById(application);
        return application;
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplication enterpriseReview(Long id, JobApplicationReviewRequest request) {
        JobApplication application = getApplication(id);
        application.setApplicationStatus("approved".equals(request.getResult()) ? "enterprise_approved" : "enterprise_rejected");
        application.setEnterpriseFeedback(request.getFeedback());
        applicationMapper.updateById(application);
        return application;
    }

    @Transactional(rollbackFor = Exception.class)
    public JobApplication recommend(Long id) {
        JobApplication application = getApplication(id);
        application.setApplicationStatus("recommended");
        applicationMapper.updateById(application);
        return application;
    }

    private JobApplication getApplication(Long id) {
        JobApplication application = applicationMapper.selectById(id);
        if (application == null || application.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "岗位投递不存在");
        }
        return application;
    }
}
