package com.example.ailearning.module.resource.dto;

import java.util.List;

public class AiGenerationTaskRequest {
    private Long targetJobRoleId;
    private Long courseId;
    private Long competitionId;
    private Long certificateId;
    private List<Long> knowledgePointIds;
    private List<Long> weakPointIds;
    private List<String> resourceTypes;
    private String difficulty = "intermediate";
    private String scenario = "personalized_learning";
    private String prompt;

    public Long getTargetJobRoleId() { return targetJobRoleId; }
    public void setTargetJobRoleId(Long targetJobRoleId) { this.targetJobRoleId = targetJobRoleId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }
    public List<Long> getKnowledgePointIds() { return knowledgePointIds; }
    public void setKnowledgePointIds(List<Long> knowledgePointIds) { this.knowledgePointIds = knowledgePointIds; }
    public List<Long> getWeakPointIds() { return weakPointIds; }
    public void setWeakPointIds(List<Long> weakPointIds) { this.weakPointIds = weakPointIds; }
    public List<String> getResourceTypes() { return resourceTypes; }
    public void setResourceTypes(List<String> resourceTypes) { this.resourceTypes = resourceTypes; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
