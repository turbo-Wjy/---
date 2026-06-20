package com.example.ailearning.module.course.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.course.dto.QuizAttemptRequest;
import com.example.ailearning.module.course.service.QuizAttemptService;
import com.example.ailearning.module.course.vo.QuizAttemptVO;
import com.example.ailearning.module.course.vo.WrongQuestionVO;
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
@RequestMapping("/api/v1")
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping("/quiz-attempts")
    @PreAuthorize("hasAuthority('quiz.practice')")
    public ApiResponse<QuizAttemptVO> create(@Valid @RequestBody QuizAttemptRequest request) {
        return ApiResponse.success(quizAttemptService.create(request));
    }

    @GetMapping("/wrong-questions/me")
    public ApiResponse<List<WrongQuestionVO>> myWrongQuestions(@RequestParam(required = false) Long knowledgePointId) {
        return ApiResponse.success(quizAttemptService.myWrongQuestions(knowledgePointId));
    }
}
