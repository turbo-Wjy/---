package com.example.ailearning.module.course.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.course.dto.KnowledgePointRequest;
import com.example.ailearning.module.course.service.KnowledgePointRelationService;
import com.example.ailearning.module.course.service.KnowledgePointService;
import com.example.ailearning.module.course.vo.KnowledgePointRelationVO;
import com.example.ailearning.module.course.vo.KnowledgePointVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge-points")
public class KnowledgePointController {
    private final KnowledgePointService knowledgePointService;
    private final KnowledgePointRelationService relationService;

    public KnowledgePointController(KnowledgePointService knowledgePointService, KnowledgePointRelationService relationService) {
        this.knowledgePointService = knowledgePointService;
        this.relationService = relationService;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('course_knowledge_point.manage')")
    public ApiResponse<KnowledgePointVO> update(@PathVariable Long id, @Valid @RequestBody KnowledgePointRequest request) {
        return ApiResponse.success(knowledgePointService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('course_knowledge_point.manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        knowledgePointService.softDelete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/relations")
    public ApiResponse<List<KnowledgePointRelationVO>> relations(@PathVariable Long id) {
        return ApiResponse.success(relationService.listByKnowledgePoint(id));
    }
}
