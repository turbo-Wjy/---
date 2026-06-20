package com.example.ailearning.module.course.vo;

public class CourseResourceVO {
    private Long id;
    private Long courseId;
    private Long knowledgePointId;
    private Long uploadedByTeacherId;
    private String resourceType;
    private String title;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public Long getUploadedByTeacherId() { return uploadedByTeacherId; }
    public void setUploadedByTeacherId(Long uploadedByTeacherId) { this.uploadedByTeacherId = uploadedByTeacherId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
