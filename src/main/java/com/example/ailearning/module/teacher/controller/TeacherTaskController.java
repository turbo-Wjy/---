package com.example.ailearning.module.teacher.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.teacher.dto.TeacherTaskRequest;
import com.example.ailearning.module.teacher.service.TeacherTaskService;
import com.example.ailearning.module.teacher.vo.TeacherTaskVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher-tasks")
public class TeacherTaskController {
    private final TeacherTaskService teacherTaskService;

    public TeacherTaskController(TeacherTaskService teacherTaskService) {
        this.teacherTaskService = teacherTaskService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<TeacherTaskVO> create(@Valid @RequestBody TeacherTaskRequest request) {
        return ApiResponse.success(teacherTaskService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<List<TeacherTaskVO>> list(@RequestParam(required = false) String publishStatus) {
        return ApiResponse.success(teacherTaskService.listMine(publishStatus));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<TeacherTaskVO> publish(@PathVariable Long id) {
        return ApiResponse.success(teacherTaskService.publish(id));
    }
}
