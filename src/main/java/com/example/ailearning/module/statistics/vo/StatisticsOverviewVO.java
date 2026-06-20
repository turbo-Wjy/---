package com.example.ailearning.module.statistics.vo;

import com.example.ailearning.module.dashboard.vo.DashboardMetricVO;

import java.util.List;

public class StatisticsOverviewVO {
    private String statisticsType;
    private Long majorId;
    private List<DashboardMetricVO> metrics;

    public String getStatisticsType() { return statisticsType; }
    public void setStatisticsType(String statisticsType) { this.statisticsType = statisticsType; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public List<DashboardMetricVO> getMetrics() { return metrics; }
    public void setMetrics(List<DashboardMetricVO> metrics) { this.metrics = metrics; }
}
