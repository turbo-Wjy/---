package com.example.ailearning.module.dashboard.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.module.dashboard.service.DashboardService;
import com.example.ailearning.module.dashboard.vo.ClassStudentOverviewVO;
import com.example.ailearning.module.dashboard.vo.ClassWeakPointVO;
import com.example.ailearning.module.dashboard.vo.DashboardItemVO;
import com.example.ailearning.module.dashboard.vo.DashboardOverviewVO;
import com.example.ailearning.module.profile.vo.LearningProfileVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/classes/{classId}/students")
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<List<ClassStudentOverviewVO>> classStudents(@PathVariable Long classId) {
        return ApiResponse.success(dashboardService.classStudents(classId));
    }

    @GetMapping("/classes/{classId}/learning-profiles")
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<List<LearningProfileVO>> classLearningProfiles(@PathVariable Long classId) {
        return ApiResponse.success(dashboardService.classLearningProfiles(classId));
    }

    @GetMapping("/classes/{classId}/weak-points")
    @PreAuthorize("hasAuthority('teacher_dashboard.view.assigned')")
    public ApiResponse<List<ClassWeakPointVO>> classWeakPoints(@PathVariable Long classId) {
        return ApiResponse.success(dashboardService.classWeakPoints(classId));
    }
}
