package com.example.ailearning.module.teacher.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.teacher.dto.TeacherStudentGroupRequest;
import com.example.ailearning.module.teacher.service.TeacherStudentGroupService;
import com.example.ailearning.module.teacher.vo.TeacherStudentGroupVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teacher-student-groups")
public class TeacherStudentGroupController {
    private final TeacherStudentGroupService service;

    public TeacherStudentGroupController(TeacherStudentGroupService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('student_profile.view_assigned','profile.view.assigned','base_data.manage')")
    public ApiResponse<PageResult<TeacherStudentGroupVO>> page(PageQuery query) {
        return ApiResponse.success(service.page(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('student_profile.view_assigned','profile.view.assigned','base_data.manage')")
    public ApiResponse<TeacherStudentGroupVO> get(@PathVariable Long id) {
        return ApiResponse.success(service.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<TeacherStudentGroupVO> create(@Valid @RequestBody TeacherStudentGroupRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<TeacherStudentGroupVO> update(@PathVariable Long id, @Valid @RequestBody TeacherStudentGroupRequest request) {
        return ApiResponse.success(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ApiResponse.success(null);
    }
}
