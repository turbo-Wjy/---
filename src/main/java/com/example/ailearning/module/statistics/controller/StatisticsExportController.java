package com.example.ailearning.module.statistics.controller;

import com.example.ailearning.common.api.ApiResponse;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.statistics.dto.StatisticsExportRequest;
import com.example.ailearning.module.statistics.service.StatisticsExportService;
import com.example.ailearning.module.statistics.vo.ExportRecordVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsExportController {
    private final StatisticsExportService statisticsExportService;

    public StatisticsExportController(StatisticsExportService statisticsExportService) {
        this.statisticsExportService = statisticsExportService;
    }

    @PostMapping("/exports")
    @PreAuthorize("hasAuthority('statistics.export_major')")
    public ApiResponse<ExportRecordVO> export(@Valid @RequestBody StatisticsExportRequest request) {
        return ApiResponse.success(statisticsExportService.export(request));
    }

    @GetMapping("/exports")
    @PreAuthorize("hasAnyAuthority('statistics.export_major','statistics.view_readonly')")
    public ApiResponse<PageResult<ExportRecordVO>> records(
            PageQuery query,
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) String exportType
    ) {
        return ApiResponse.success(statisticsExportService.records(query, majorId, exportType));
    }
}
