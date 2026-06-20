package com.example.ailearning.module.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.dto.CourseResourceRequest;
import com.example.ailearning.module.course.entity.CourseResource;
import com.example.ailearning.module.course.mapper.CourseResourceMapper;
import com.example.ailearning.module.course.vo.CourseResourceVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseResourceService {
    private final CourseService courseService;
    private final KnowledgePointService knowledgePointService;
    private final CourseResourceMapper resourceMapper;

    public CourseResourceService(CourseService courseService, KnowledgePointService knowledgePointService, CourseResourceMapper resourceMapper) {
        this.courseService = courseService;
        this.knowledgePointService = knowledgePointService;
        this.resourceMapper = resourceMapper;
    }

    public PageResult<CourseResourceVO> page(PageQuery query, Long courseId, Long knowledgePointId, String resourceType) {
        Page<CourseResource> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<CourseResource> wrapper = new LambdaQueryWrapper<CourseResource>()
                .isNull(CourseResource::getDeletedAt)
                .orderByDesc(CourseResource::getCreatedAt);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(CourseResource::getTitle, query.getKeyword());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(CourseResource::getStatus, query.getStatus());
        }
        if (courseId != null) {
            wrapper.eq(CourseResource::getCourseId, courseId);
        }
        if (knowledgePointId != null) {
            wrapper.eq(CourseResource::getKnowledgePointId, knowledgePointId);
        }
        if (resourceType != null && !resourceType.isBlank()) {
            wrapper.eq(CourseResource::getResourceType, resourceType);
        }
        Page<CourseResource> result = resourceMapper.selectPage(page, wrapper);
        List<CourseResourceVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public CourseResourceVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public CourseResourceVO create(CourseResourceRequest request) {
        courseService.getEntity(request.getCourseId());
        if (request.getKnowledgePointId() != null) {
            knowledgePointService.getEntity(request.getKnowledgePointId());
        }
        CourseResource resource = new CourseResource();
        fill(resource, request);
        resource.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        resourceMapper.insert(resource);
        return toVO(resource);
    }

    @Transactional(rollbackFor = Exception.class)
    public CourseResourceVO update(Long id, CourseResourceRequest request) {
        CourseResource resource = getEntity(id);
        courseService.getEntity(request.getCourseId());
        if (request.getKnowledgePointId() != null) {
            knowledgePointService.getEntity(request.getKnowledgePointId());
        }
        fill(resource, request);
        resourceMapper.updateById(resource);
        return toVO(resource);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        CourseResource resource = getEntity(id);
        resource.setDeletedAt(DeleteConstants.now());
        resource.setStatus("deleted");
        resourceMapper.updateById(resource);
    }

    private CourseResource getEntity(Long id) {
        CourseResource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "课程资料不存在");
        }
        return resource;
    }

    private void fill(CourseResource resource, CourseResourceRequest request) {
        resource.setCourseId(request.getCourseId());
        resource.setKnowledgePointId(request.getKnowledgePointId());
        resource.setUploadedByTeacherId(request.getUploadedByTeacherId());
        resource.setResourceType(request.getResourceType());
        resource.setTitle(request.getTitle());
        resource.setFileUrl(request.getFileUrl());
        resource.setFileName(request.getFileName());
        resource.setFileType(request.getFileType());
        resource.setFileSize(request.getFileSize());
        resource.setStatus(request.getStatus());
    }

    private CourseResourceVO toVO(CourseResource resource) {
        CourseResourceVO vo = new CourseResourceVO();
        vo.setId(resource.getId());
        vo.setCourseId(resource.getCourseId());
        vo.setKnowledgePointId(resource.getKnowledgePointId());
        vo.setUploadedByTeacherId(resource.getUploadedByTeacherId());
        vo.setResourceType(resource.getResourceType());
        vo.setTitle(resource.getTitle());
        vo.setFileUrl(resource.getFileUrl());
        vo.setFileName(resource.getFileName());
        vo.setFileType(resource.getFileType());
        vo.setFileSize(resource.getFileSize());
        vo.setStatus(resource.getStatus());
        return vo;
    }
}
