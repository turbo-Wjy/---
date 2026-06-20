package com.example.ailearning.module.competition.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.audit.dto.ReviewRequest;
import com.example.ailearning.module.audit.service.AchievementReviewService;
import com.example.ailearning.module.competition.dto.CompetitionRequest;
import com.example.ailearning.module.competition.dto.CompetitionResultRequest;
import com.example.ailearning.module.competition.entity.Competition;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.service.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CompetitionController {
    private final CompetitionService competitionService;
    private final AchievementReviewService reviewService;

    public CompetitionController(CompetitionService competitionService, AchievementReviewService reviewService) {
        this.competitionService = competitionService;
        this.reviewService = reviewService;
    }

    @GetMapping("/competitions")
    public ApiResponse<List<Competition>> competitions(@RequestParam(required = false) String status) {
        return ApiResponse.success(competitionService.list(status));
    }

    @PostMapping("/competitions")
    @PreAuthorize("hasAuthority('competition.publish')")
    public ApiResponse<Competition> createCompetition(@Valid @RequestBody CompetitionRequest request) {
        return ApiResponse.success(competitionService.create(request));
    }

    @GetMapping("/competition-results")
    public ApiResponse<List<CompetitionResult>> competitionResults(@RequestParam(required = false) String reviewStatus) {
        return ApiResponse.success(competitionService.listResults(reviewStatus));
    }

    @PostMapping("/competition-results")
    @PreAuthorize("hasAnyAuthority('competition.publish','competition_result.upload_coached')")
    public ApiResponse<CompetitionResult> submitCompetitionResult(@Valid @RequestBody CompetitionResultRequest request) {
        return ApiResponse.success(competitionService.submitResult(request));
    }

    @PostMapping("/competition-results/{id}/review")
    @PreAuthorize("hasAnyAuthority('competition.publish','competition_result.review')")
    public ApiResponse<CompetitionResult> reviewCompetitionResult(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(reviewService.reviewCompetitionResult(id, request));
    }
}
