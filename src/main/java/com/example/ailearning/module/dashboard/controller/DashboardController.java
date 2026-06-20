package com.example.ailearning.module.dashboard.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.dashboard.service.DashboardService;
import com.example.ailearning.module.dashboard.vo.DashboardOverviewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewVO> overview() {
        return ApiResponse.success(dashboardService.overview());
    }
}
