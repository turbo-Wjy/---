package com.example.ailearning.module.learning.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.learning.service.LearningPathService;
import com.example.ailearning.module.learning.vo.ResourceRecommendationVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resource-recommendations")
public class ResourceRecommendationController {
    private final LearningPathService learningPathService;

    public ResourceRecommendationController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('resource_recommendation.view_self','resource_recommendation.view.self')")
    public ApiResponse<List<ResourceRecommendationVO>> myRecommendations(@RequestParam(required = false) String viewStatus) {
        return ApiResponse.success(learningPathService.myRecommendations(viewStatus));
    }
}
