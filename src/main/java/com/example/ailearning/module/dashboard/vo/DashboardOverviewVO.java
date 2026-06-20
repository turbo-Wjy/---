package com.example.ailearning.module.dashboard.vo;

import com.example.ailearning.module.learning.vo.ResourceRecommendationVO;

import java.util.List;

public class DashboardOverviewVO {
    private String dashboardType;
    private String greetingName;
    private List<DashboardMetricVO> metrics;
    private List<DashboardItemVO> todayTasks;
    private List<DashboardItemVO> learningReminders;
    private List<ResourceRecommendationVO> recommendedResources;
    private List<DashboardItemVO> pendingReviews;
    private LearningPathProgressVO learningPathProgress;

    public String getDashboardType() { return dashboardType; }
    public void setDashboardType(String dashboardType) { this.dashboardType = dashboardType; }
    public String getGreetingName() { return greetingName; }
    public void setGreetingName(String greetingName) { this.greetingName = greetingName; }
    public List<DashboardMetricVO> getMetrics() { return metrics; }
    public void setMetrics(List<DashboardMetricVO> metrics) { this.metrics = metrics; }
    public List<DashboardItemVO> getTodayTasks() { return todayTasks; }
    public void setTodayTasks(List<DashboardItemVO> todayTasks) { this.todayTasks = todayTasks; }
    public List<DashboardItemVO> getLearningReminders() { return learningReminders; }
    public void setLearningReminders(List<DashboardItemVO> learningReminders) { this.learningReminders = learningReminders; }
    public List<ResourceRecommendationVO> getRecommendedResources() { return recommendedResources; }
    public void setRecommendedResources(List<ResourceRecommendationVO> recommendedResources) { this.recommendedResources = recommendedResources; }
    public List<DashboardItemVO> getPendingReviews() { return pendingReviews; }
    public void setPendingReviews(List<DashboardItemVO> pendingReviews) { this.pendingReviews = pendingReviews; }
    public LearningPathProgressVO getLearningPathProgress() { return learningPathProgress; }
    public void setLearningPathProgress(LearningPathProgressVO learningPathProgress) { this.learningPathProgress = learningPathProgress; }
}
