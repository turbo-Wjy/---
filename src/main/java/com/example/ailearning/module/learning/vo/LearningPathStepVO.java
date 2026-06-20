package com.example.ailearning.module.learning.vo;

import com.example.ailearning.module.resource.vo.AiGeneratedResourceVO;

public class LearningPathStepVO {
    private Long id;
    private Long pathId;
    private Integer stepOrder;
    private String title;
    private Long resourceId;
    private Integer expectedDuration;
    private String completionStatus;
    private String status;
    private AiGeneratedResourceVO resource;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public Integer getExpectedDuration() { return expectedDuration; }
    public void setExpectedDuration(Integer expectedDuration) { this.expectedDuration = expectedDuration; }
    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public AiGeneratedResourceVO getResource() { return resource; }
    public void setResource(AiGeneratedResourceVO resource) { this.resource = resource; }
}
