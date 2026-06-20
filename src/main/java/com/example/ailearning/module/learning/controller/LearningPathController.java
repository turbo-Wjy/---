package com.example.ailearning.module.learning.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.learning.dto.LearningPathAdjustRequest;
import com.example.ailearning.module.learning.dto.LearningPathGenerateRequest;
import com.example.ailearning.module.learning.service.LearningPathService;
import com.example.ailearning.module.learning.vo.LearningPathVO;
import com.example.ailearning.module.resource.vo.AiGeneratedResourceVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning-paths")
public class LearningPathController {
    private final LearningPathService learningPathService;

    public LearningPathController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('resource_package.generate.self')")
    public ApiResponse<LearningPathVO> generate(@RequestBody LearningPathGenerateRequest request) {
        return ApiResponse.success(learningPathService.generate(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('learning_path.view_self','learning_path.view.self')")
    public ApiResponse<List<LearningPathVO>> myPaths() {
        return ApiResponse.success(learningPathService.myPaths());
    }

    @GetMapping("/{id}")
    public ApiResponse<LearningPathVO> get(@PathVariable Long id) {
        return ApiResponse.success(learningPathService.get(id));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyAuthority('learning_path.view_self','learning_path.view.self')")
    public ApiResponse<LearningPathVO> accept(@PathVariable Long id) {
        return ApiResponse.success(learningPathService.accept(id));
    }

    @PostMapping("/{id}/adjust")
    @PreAuthorize("hasAnyAuthority('learning_path.view_self','learning_path.view.self')")
    public ApiResponse<LearningPathVO> adjust(@PathVariable Long id, @RequestBody LearningPathAdjustRequest request) {
        return ApiResponse.success(learningPathService.adjust(id, request));
    }

    @GetMapping("/{id}/resources")
    public ApiResponse<List<AiGeneratedResourceVO>> resources(@PathVariable Long id) {
        return ApiResponse.success(learningPathService.pathResources(id));
    }
}
