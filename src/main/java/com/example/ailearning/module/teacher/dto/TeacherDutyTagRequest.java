package com.example.ailearning.module.teacher.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class TeacherDutyTagRequest {
    @Valid
    @NotEmpty
    private List<TagItem> tags;

    public List<TagItem> getTags() { return tags; }
    public void setTags(List<TagItem> tags) { this.tags = tags; }

    public static class TagItem {
        private String tagCode;
        private String tagName;

        public String getTagCode() { return tagCode; }
        public void setTagCode(String tagCode) { this.tagCode = tagCode; }
        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
    }
}
