package com.example.ailearning.module.evaluation.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.evaluation.dto.LearningEvaluationGenerateRequest;
import com.example.ailearning.module.evaluation.service.LearningEvaluationService;
import com.example.ailearning.module.evaluation.vo.LearningEvaluationVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning-evaluations")
public class LearningEvaluationController {
    private final LearningEvaluationService learningEvaluationService;

    public LearningEvaluationController(LearningEvaluationService learningEvaluationService) {
        this.learningEvaluationService = learningEvaluationService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('learning_effect.generate.self','learning_effect.generate_self')")
    public ApiResponse<LearningEvaluationVO> generate(@RequestBody LearningEvaluationGenerateRequest request) {
        return ApiResponse.success(learningEvaluationService.generate(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('learning_effect.view_self','learning_effect.view.self')")
    public ApiResponse<List<LearningEvaluationVO>> myEvaluations() {
        return ApiResponse.success(learningEvaluationService.myEvaluations());
    }
}
