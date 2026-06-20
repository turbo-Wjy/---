package com.example.ailearning.module.course.vo;

public class CourseProgressVO {
    private Long courseId;
    private Long studentId;
    private long totalKnowledgePoints;
    private long completedLearningRecords;
    private long totalDurationSeconds;

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public long getTotalKnowledgePoints() { return totalKnowledgePoints; }
    public void setTotalKnowledgePoints(long totalKnowledgePoints) { this.totalKnowledgePoints = totalKnowledgePoints; }
    public long getCompletedLearningRecords() { return completedLearningRecords; }
    public void setCompletedLearningRecords(long completedLearningRecords) { this.completedLearningRecords = completedLearningRecords; }
    public long getTotalDurationSeconds() { return totalDurationSeconds; }
    public void setTotalDurationSeconds(long totalDurationSeconds) { this.totalDurationSeconds = totalDurationSeconds; }
}
