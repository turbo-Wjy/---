package com.example.ailearning.module.course.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.course.dto.KnowledgePointRelationRequest;
import com.example.ailearning.module.course.service.KnowledgePointRelationService;
import com.example.ailearning.module.course.vo.KnowledgePointRelationVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/knowledge-point-relations")
public class KnowledgePointRelationController {
    private final KnowledgePointRelationService relationService;

    public KnowledgePointRelationController(KnowledgePointRelationService relationService) {
        this.relationService = relationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('course_knowledge_point.manage')")
    public ApiResponse<KnowledgePointRelationVO> create(@Valid @RequestBody KnowledgePointRelationRequest request) {
        return ApiResponse.success(relationService.create(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('course_knowledge_point.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        relationService.softDelete(id);
        return ApiResponse.success(null);
    }
}
