package com.example.ailearning.module.base.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.base.dto.SchoolClassRequest;
import com.example.ailearning.module.base.service.SchoolClassService;
import com.example.ailearning.module.base.vo.SchoolClassVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/classes")
public class SchoolClassController {
    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public ApiResponse<PageResult<SchoolClassVO>> page(PageQuery query) {
        return ApiResponse.success(schoolClassService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<SchoolClassVO> get(@PathVariable Long id) {
        return ApiResponse.success(schoolClassService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<SchoolClassVO> create(@Valid @RequestBody SchoolClassRequest request) {
        return ApiResponse.success(schoolClassService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<SchoolClassVO> update(@PathVariable Long id, @Valid @RequestBody SchoolClassRequest request) {
        return ApiResponse.success(schoolClassService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        schoolClassService.softDelete(id);
        return ApiResponse.success(null);
    }
}
