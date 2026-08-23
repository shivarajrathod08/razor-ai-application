package com.hackathon.dto;

public class RevenueInsightDto {
    private String id;
    private String title;
    private String description;
    private String impact; // e.g. "High", "Medium", "Positive"
    private String category;
    private String metricHighlight;

    public RevenueInsightDto() {}

    public RevenueInsightDto(String id, String title, String description, String impact, String category, String metricHighlight) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.impact = impact;
        this.category = category;
        this.metricHighlight = metricHighlight;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMetricHighlight() { return metricHighlight; }
    public void setMetricHighlight(String metricHighlight) { this.metricHighlight = metricHighlight; }
}