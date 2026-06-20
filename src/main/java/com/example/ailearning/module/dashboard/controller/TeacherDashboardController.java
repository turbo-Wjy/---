package com.example.ailearning.module.dashboard.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.dashboard.service.DashboardService;
import com.example.ailearning.module.dashboard.vo.DashboardItemVO;
import com.example.ailearning.module.dashboard.vo.DashboardOverviewVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher-dashboard")
public class TeacherDashboardController {
    private final DashboardService dashboardService;

    public TeacherDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<DashboardOverviewVO> overview() {
        return ApiResponse.success(dashboardService.teacherOverview());
    }

    @GetMapping("/pending-reviews")
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<List<DashboardItemVO>> pendingReviews() {
        return ApiResponse.success(dashboardService.teacherPendingReviews());
    }
}
