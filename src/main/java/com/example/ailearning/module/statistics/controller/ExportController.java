package com.example.ailearning.module.statistics.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.statistics.dto.ExportRequest;
import com.example.ailearning.module.statistics.service.StatisticsService;
import com.example.ailearning.module.statistics.vo.ExportRecordVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exports")
public class ExportController {
    private final StatisticsService statisticsService;

    public ExportController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('statistics.export_major')")
    public ApiResponse<ExportRecordVO> create(@Valid @RequestBody ExportRequest request) {
        return ApiResponse.success(statisticsService.createExport(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('statistics.export_major')")
    public ApiResponse<PageResult<ExportRecordVO>> list(PageQuery query) {
        return ApiResponse.success(statisticsService.pageExports(query));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAuthority('statistics.export_major')")
    public ApiResponse<ExportRecordVO> download(@PathVariable Long id) {
        return ApiResponse.success(statisticsService.getExport(id));
    }
}
