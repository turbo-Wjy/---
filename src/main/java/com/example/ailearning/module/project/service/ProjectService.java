package com.example.ailearning.module.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.project.dto.ProjectDeliverableRequest;
import com.example.ailearning.module.project.dto.ProjectRequest;
import com.example.ailearning.module.project.entity.Project;
import com.example.ailearning.module.project.entity.ProjectDeliverable;
import com.example.ailearning.module.project.mapper.ProjectDeliverableMapper;
import com.example.ailearning.module.project.mapper.ProjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectMapper projectMapper;
    private final ProjectDeliverableMapper deliverableMapper;

    public ProjectService(ProjectMapper projectMapper, ProjectDeliverableMapper deliverableMapper) {
        this.projectMapper = projectMapper;
        this.deliverableMapper = deliverableMapper;
    }

    public List<Project> list(Long courseId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .isNull(Project::getDeletedAt)
                .orderByDesc(Project::getCreatedAt);
        if (courseId != null) {
            wrapper.eq(Project::getCourseId, courseId);
        }
        return projectMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public Project create(ProjectRequest request) {
        Project project = new Project();
        project.setCourseId(request.getCourseId());
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setDifficultyLevel(request.getDifficultyLevel());
        project.setAbilityTagsJson(request.getAbilityTags());
        project.setStatus("active");
        project.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        projectMapper.insert(project);
        return project;
    }

    public List<ProjectDeliverable> listDeliverables(String reviewStatus) {
        LambdaQueryWrapper<ProjectDeliverable> wrapper = new LambdaQueryWrapper<ProjectDeliverable>()
                .isNull(ProjectDeliverable::getDeletedAt)
                .orderByDesc(ProjectDeliverable::getCreatedAt);
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(ProjectDeliverable::getReviewStatus, reviewStatus);
        }
        return deliverableMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProjectDeliverable submitDeliverable(ProjectDeliverableRequest request) {
        ProjectDeliverable deliverable = new ProjectDeliverable();
        deliverable.setProjectId(request.getProjectId());
        deliverable.setTaskId(request.getTaskId());
        deliverable.setMilestoneId(request.getMilestoneId());
        deliverable.setStudentId(request.getStudentId());
        deliverable.setTitle(request.getTitle());
        deliverable.setDeliverableType(request.getDeliverableType());
        deliverable.setFileUrl(request.getFileUrl());
        deliverable.setContentText(request.getContentText());
        deliverable.setSubmittedAt(LocalDateTime.now());
        deliverable.setReviewStatus("pending");
        deliverable.setStatus("submitted");
        deliverable.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        deliverableMapper.insert(deliverable);
        return deliverable;
    }
}
