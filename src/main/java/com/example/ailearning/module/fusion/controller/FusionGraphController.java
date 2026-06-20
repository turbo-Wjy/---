package com.example.ailearning.module.fusion.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.fusion.service.FusionGraphService;
import com.example.ailearning.module.fusion.vo.FusionGraphVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fusion-graph")
public class FusionGraphController {
    private final FusionGraphService graphService;

    public FusionGraphController(FusionGraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('fusion.graph.view.self')")
    public ApiResponse<FusionGraphVO> myGraph(@RequestParam(required = false) Long jobRoleId) {
        return ApiResponse.success(graphService.myGraph(jobRoleId));
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAuthority('fusion.graph.view.assigned')")
    public ApiResponse<FusionGraphVO> studentGraph(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long jobRoleId
    ) {
        return ApiResponse.success(graphService.assignedStudentGraph(studentId, jobRoleId));
    }

    @GetMapping("/jobs/{jobRoleId}")
    public ApiResponse<FusionGraphVO> jobGraph(@PathVariable Long jobRoleId) {
        return ApiResponse.success(graphService.jobGraph(jobRoleId));
    }

    @GetMapping("/courses/{courseId}")
    public ApiResponse<FusionGraphVO> courseGraph(@PathVariable Long courseId) {
        return ApiResponse.success(graphService.courseGraph(courseId));
    }
}
