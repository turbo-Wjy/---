package com.example.ailearning.module.fusion.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.fusion.dto.FusionRelationQuery;
import com.example.ailearning.module.fusion.dto.FusionRelationRequest;
import com.example.ailearning.module.fusion.service.FusionRelationService;
import com.example.ailearning.module.fusion.vo.FusionRelationVO;
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
@RequestMapping("/api/v1/fusion-relations")
public class FusionRelationController {
    private final FusionRelationService relationService;

    public FusionRelationController(FusionRelationService relationService) {
        this.relationService = relationService;
    }

    @GetMapping
    public ApiResponse<PageResult<FusionRelationVO>> page(FusionRelationQuery query) {
        return ApiResponse.success(relationService.page(query));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('fusion.relation.manage')")
    public ApiResponse<FusionRelationVO> create(@Valid @RequestBody FusionRelationRequest request) {
        return ApiResponse.success(relationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('fusion.relation.manage')")
    public ApiResponse<FusionRelationVO> update(@PathVariable Long id, @Valid @RequestBody FusionRelationRequest request) {
        return ApiResponse.success(relationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('fusion.relation.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        relationService.softDelete(id);
        return ApiResponse.success(null);
    }
}
