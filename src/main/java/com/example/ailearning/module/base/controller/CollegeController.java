package com.example.ailearning.module.base.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.base.dto.CollegeRequest;
import com.example.ailearning.module.base.service.CollegeService;
import com.example.ailearning.module.base.vo.CollegeVO;
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
@RequestMapping("/api/v1/colleges")
public class CollegeController {
    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @GetMapping
    public ApiResponse<PageResult<CollegeVO>> page(PageQuery query) {
        return ApiResponse.success(collegeService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<CollegeVO> get(@PathVariable Long id) {
        return ApiResponse.success(collegeService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<CollegeVO> create(@Valid @RequestBody CollegeRequest request) {
        return ApiResponse.success(collegeService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<CollegeVO> update(@PathVariable Long id, @Valid @RequestBody CollegeRequest request) {
        return ApiResponse.success(collegeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        collegeService.softDelete(id);
        return ApiResponse.success(null);
    }
}
