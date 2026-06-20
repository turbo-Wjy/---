package com.example.ailearning.module.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.dto.CourseRequest;
import com.example.ailearning.module.course.entity.Course;
import com.example.ailearning.module.course.mapper.CourseMapper;
import com.example.ailearning.module.course.vo.CourseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {
    private final CourseMapper courseMapper;

    public CourseService(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    public PageResult<CourseVO> page(PageQuery query, Long majorId) {
        Page<Course> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .isNull(Course::getDeletedAt)
                .orderByAsc(Course::getCourseCode);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(Course::getCourseCode, query.getKeyword()).or().like(Course::getCourseName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Course::getStatus, query.getStatus());
        }
        if (majorId != null) {
            wrapper.eq(Course::getMajorId, majorId);
        }
        Page<Course> result = courseMapper.selectPage(page, wrapper);
        List<CourseVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public CourseVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public CourseVO create(CourseRequest request) {
        boolean exists = courseMapper.exists(new LambdaQueryWrapper<Course>()
                .eq(Course::getCourseCode, request.getCourseCode())
                .isNull(Course::getDeletedAt));
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT, "课程编码已存在");
        }
        Course course = new Course();
        fill(course, request);
        course.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        courseMapper.insert(course);
        return toVO(course);
    }

    @Transactional(rollbackFor = Exception.class)
    public CourseVO update(Long id, CourseRequest request) {
        Course course = getEntity(id);
        fill(course, request);
        courseMapper.updateById(course);
        return toVO(course);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Course course = getEntity(id);
        course.setDeletedAt(DeleteConstants.now());
        course.setStatus("deleted");
        courseMapper.updateById(course);
    }

    Course getEntity(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null || course.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "课程不存在");
        }
        return course;
    }

    private void fill(Course course, CourseRequest request) {
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setMajorId(request.getMajorId());
        course.setCredit(request.getCredit());
        course.setSemester(request.getSemester());
        course.setStatus(request.getStatus());
    }

    CourseVO toVO(Course course) {
        CourseVO vo = new CourseVO();
        vo.setId(course.getId());
        vo.setCourseCode(course.getCourseCode());
        vo.setCourseName(course.getCourseName());
        vo.setMajorId(course.getMajorId());
        vo.setCredit(course.getCredit());
        vo.setSemester(course.getSemester());
        vo.setStatus(course.getStatus());
        return vo;
    }
}
