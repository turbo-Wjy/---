package com.example.ailearning.module.course.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.course.dto.LearningRecordRequest;
import com.example.ailearning.module.course.service.LearningRecordService;
import com.example.ailearning.module.course.vo.LearningRecordVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning-records")
public class LearningRecordController {
    private final LearningRecordService learningRecordService;

    public LearningRecordController(LearningRecordService learningRecordService) {
        this.learningRecordService = learningRecordService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('learning_record.create_self')")
    public ApiResponse<LearningRecordVO> create(@Valid @RequestBody LearningRecordRequest request) {
        return ApiResponse.success(learningRecordService.create(request));
    }

    @GetMapping("/me")
    public ApiResponse<List<LearningRecordVO>> myRecords(@RequestParam(required = false) Long courseId) {
        return ApiResponse.success(learningRecordService.myRecords(courseId));
    }
}
