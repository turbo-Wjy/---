package com.example.ailearning.module.dashboard.vo;

public class LearningPathProgressVO {
    private Long pathId;
    private String pathTitle;
    private String pathStatus;
    private long totalSteps;
    private long completedSteps;
    private int progressPercent;

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }
    public String getPathTitle() { return pathTitle; }
    public void setPathTitle(String pathTitle) { this.pathTitle = pathTitle; }
    public String getPathStatus() { return pathStatus; }
    public void setPathStatus(String pathStatus) { this.pathStatus = pathStatus; }
    public long getTotalSteps() { return totalSteps; }
    public void setTotalSteps(long totalSteps) { this.totalSteps = totalSteps; }
    public long getCompletedSteps() { return completedSteps; }
    public void setCompletedSteps(long completedSteps) { this.completedSteps = completedSteps; }
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
}
