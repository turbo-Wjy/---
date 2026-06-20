package com.example.ailearning.module.learning.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.learning.service.LearningPathService;
import com.example.ailearning.module.learning.vo.LearningPathStepVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/learning-path-steps")
public class LearningPathStepController {
    private final LearningPathService learningPathService;

    public LearningPathStepController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('learning_record.create_self')")
    public ApiResponse<LearningPathStepVO> complete(@PathVariable Long id) {
        return ApiResponse.success(learningPathService.completeStep(id));
    }
}
