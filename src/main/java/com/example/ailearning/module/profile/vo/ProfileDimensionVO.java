package com.example.ailearning.module.profile.vo;

import java.math.BigDecimal;

public class ProfileDimensionVO {
    private String code;
    private String name;
    private String value;
    private BigDecimal confidence;
    private String source;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
