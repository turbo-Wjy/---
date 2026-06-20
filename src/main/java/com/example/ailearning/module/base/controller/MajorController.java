package com.example.ailearning.module.base.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.base.dto.MajorRequest;
import com.example.ailearning.module.base.service.MajorService;
import com.example.ailearning.module.base.vo.MajorVO;
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
@RequestMapping("/api/v1/majors")
public class MajorController {
    private final MajorService majorService;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @GetMapping
    public ApiResponse<PageResult<MajorVO>> page(PageQuery query) {
        return ApiResponse.success(majorService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<MajorVO> get(@PathVariable Long id) {
        return ApiResponse.success(majorService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<MajorVO> create(@Valid @RequestBody MajorRequest request) {
        return ApiResponse.success(majorService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<MajorVO> update(@PathVariable Long id, @Valid @RequestBody MajorRequest request) {
        return ApiResponse.success(majorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base_data.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        majorService.softDelete(id);
        return ApiResponse.success(null);
    }
}
