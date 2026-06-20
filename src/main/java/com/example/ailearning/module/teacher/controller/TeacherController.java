package com.example.ailearning.module.teacher.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.teacher.dto.TeacherDutyTagRequest;
import com.example.ailearning.module.teacher.dto.TeacherPageQuery;
import com.example.ailearning.module.teacher.dto.TeacherRequest;
import com.example.ailearning.module.teacher.service.TeacherService;
import com.example.ailearning.module.teacher.vo.TeacherVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<PageResult<TeacherVO>> page(TeacherPageQuery query) {
        return ApiResponse.success(teacherService.page(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<TeacherVO> get(@PathVariable Long id) {
        return ApiResponse.success(teacherService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<TeacherVO> create(@Valid @RequestBody TeacherRequest request) {
        return ApiResponse.success(teacherService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<TeacherVO> update(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        return ApiResponse.success(teacherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        teacherService.softDelete(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/duty-tags")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<Void> updateDutyTags(@PathVariable Long id, @Valid @RequestBody TeacherDutyTagRequest request) {
        teacherService.updateDutyTags(id, request);
        return ApiResponse.success(null);
    }
}
