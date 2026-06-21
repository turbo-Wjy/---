package com.example.ailearning.module.competition.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.achievement.dto.ReviewRequest;
import com.example.ailearning.module.competition.dto.CompetitionResultRequest;
import com.example.ailearning.module.competition.service.CompetitionResultService;
import com.example.ailearning.module.competition.vo.CompetitionResultVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/competition-results")
public class CompetitionResultController {
    private final CompetitionResultService competitionResultService;

    public CompetitionResultController(CompetitionResultService competitionResultService) {
        this.competitionResultService = competitionResultService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('competition_result.review','competition_result.upload_coached','student_profile.view_major','student_profile.view_assigned')")
    public ApiResponse<PageResult<CompetitionResultVO>> page(
            PageQuery query,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long competitionId,
            @RequestParam(required = false) String reviewStatus
    ) {
        return ApiResponse.success(competitionResultService.page(query, studentId, competitionId, reviewStatus));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('competition_result.upload_coached')")
    public ApiResponse<CompetitionResultVO> submit(@Valid @RequestBody CompetitionResultRequest request) {
        return ApiResponse.success(competitionResultService.submit(request));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('competition_result.review')")
    public ApiResponse<CompetitionResultVO> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(competitionResultService.review(id, request));
    }
}
