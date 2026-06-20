package com.example.ailearning.module.dashboard.vo;

public class DashboardMetricVO {
    private String code;
    private String name;
    private Object value;
    private String unit;

    public DashboardMetricVO() {
    }

    public DashboardMetricVO(String code, String name, Object value, String unit) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
