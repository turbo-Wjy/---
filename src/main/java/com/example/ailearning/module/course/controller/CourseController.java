package com.example.ailearning.module.course.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.course.dto.CourseRequest;
import com.example.ailearning.module.course.dto.KnowledgePointRequest;
import com.example.ailearning.module.course.service.CourseService;
import com.example.ailearning.module.course.service.KnowledgePointService;
import com.example.ailearning.module.course.service.LearningRecordService;
import com.example.ailearning.module.course.vo.CourseGraphVO;
import com.example.ailearning.module.course.vo.CourseProgressVO;
import com.example.ailearning.module.course.vo.CourseVO;
import com.example.ailearning.module.course.vo.KnowledgePointVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;
    private final KnowledgePointService knowledgePointService;
    private final LearningRecordService learningRecordService;

    public CourseController(
            CourseService courseService,
            KnowledgePointService knowledgePointService,
            LearningRecordService learningRecordService
    ) {
        this.courseService = courseService;
        this.knowledgePointService = knowledgePointService;
        this.learningRecordService = learningRecordService;
    }

    @GetMapping
    public ApiResponse<PageResult<CourseVO>> page(PageQuery query, @RequestParam(required = false) Long majorId) {
        return ApiResponse.success(courseService.page(query, majorId));
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseVO> get(@PathVariable Long id) {
        return ApiResponse.success(courseService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<CourseVO> create(@Valid @RequestBody CourseRequest request) {
        return ApiResponse.success(courseService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<CourseVO> update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ApiResponse.success(courseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        courseService.softDelete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/knowledge-points")
    public ApiResponse<List<KnowledgePointVO>> listKnowledgePoints(@PathVariable Long id) {
        return ApiResponse.success(knowledgePointService.listByCourse(id));
    }

    @PostMapping("/{id}/knowledge-points")
    @PreAuthorize("hasAuthority('course_knowledge_point.manage')")
    public ApiResponse<KnowledgePointVO> createKnowledgePoint(@PathVariable Long id, @Valid @RequestBody KnowledgePointRequest request) {
        return ApiResponse.success(knowledgePointService.create(id, request));
    }

    @GetMapping("/{id}/graph")
    public ApiResponse<CourseGraphVO> graph(@PathVariable Long id) {
        return ApiResponse.success(knowledgePointService.graph(id));
    }

    @GetMapping("/{id}/progress/me")
    public ApiResponse<CourseProgressVO> myProgress(@PathVariable Long id) {
        return ApiResponse.success(learningRecordService.myCourseProgress(id));
    }
}
