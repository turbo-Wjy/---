package com.example.ailearning.module.competition.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.competition.dto.CompetitionRequest;
import com.example.ailearning.module.competition.service.CompetitionService;
import com.example.ailearning.module.competition.vo.CompetitionVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/competitions")
public class CompetitionController {
    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping
    public ApiResponse<PageResult<CompetitionVO>> page(PageQuery query, @RequestParam(required = false) String level) {
        return ApiResponse.success(competitionService.page(query, level));
    }

    @GetMapping("/{id}")
    public ApiResponse<CompetitionVO> get(@PathVariable Long id) {
        return ApiResponse.success(competitionService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('competition.publish')")
    public ApiResponse<CompetitionVO> create(@Valid @RequestBody CompetitionRequest request) {
        return ApiResponse.success(competitionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('competition.publish')")
    public ApiResponse<CompetitionVO> update(@PathVariable Long id, @Valid @RequestBody CompetitionRequest request) {
        return ApiResponse.success(competitionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('competition.publish')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        competitionService.softDelete(id);
        return ApiResponse.success(null);
    }
}
