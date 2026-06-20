package com.example.ailearning.module.statistics.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.statistics.service.StatisticsService;
import com.example.ailearning.module.statistics.vo.StatisticsOverviewVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('statistics.view_readonly','statistics.export_major')")
    public ApiResponse<StatisticsOverviewVO> profile(@RequestParam(required = false) Long majorId) {
        return ApiResponse.success(statisticsService.profileStatistics(majorId));
    }

    @GetMapping("/fusion")
    @PreAuthorize("hasAnyAuthority('statistics.view_readonly','statistics.export_major')")
    public ApiResponse<StatisticsOverviewVO> fusion(@RequestParam(required = false) Long majorId) {
        return ApiResponse.success(statisticsService.fusionStatistics(majorId));
    }

    @GetMapping("/learning-effect")
    @PreAuthorize("hasAnyAuthority('statistics.view_readonly','statistics.export_major')")
    public ApiResponse<StatisticsOverviewVO> learningEffect(@RequestParam(required = false) Long majorId) {
        return ApiResponse.success(statisticsService.learningEffectStatistics(majorId));
    }
}
