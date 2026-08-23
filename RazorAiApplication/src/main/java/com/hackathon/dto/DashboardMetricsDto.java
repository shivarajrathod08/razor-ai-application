package com.hackathon.dto;


import java.math.BigDecimal;
import java.util.List;

public class DashboardMetricsDto {
    private BigDecimal totalRevenue;
    private BigDecimal aiAssistedRevenue;
    private BigDecimal upsellRevenue;
    private BigDecimal nonAiRevenue;
    private long totalOrders;
    private long aiAssistedOrders;
    private long upsellOrders;
    private BigDecimal averageOrderValue;
    private double conversionRate;
    private double paymentSuccessRate;
    private double upsellConversionRate;
    private List<RevenueInsightDto> insights;

    public DashboardMetricsDto() {}

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getAiAssistedRevenue() { return aiAssistedRevenue; }
    public void setAiAssistedRevenue(BigDecimal aiAssistedRevenue) { this.aiAssistedRevenue = aiAssistedRevenue; }
    public BigDecimal getUpsellRevenue() { return upsellRevenue; }
    public void setUpsellRevenue(BigDecimal upsellRevenue) { this.upsellRevenue = upsellRevenue; }
    public BigDecimal getNonAiRevenue() { return nonAiRevenue; }
    public void setNonAiRevenue(BigDecimal nonAiRevenue) { this.nonAiRevenue = nonAiRevenue; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getAiAssistedOrders() { return aiAssistedOrders; }
    public void setAiAssistedOrders(long aiAssistedOrders) { this.aiAssistedOrders = aiAssistedOrders; }
    public long getUpsellOrders() { return upsellOrders; }
    public void setUpsellOrders(long upsellOrders) { this.upsellOrders = upsellOrders; }
    public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    public double getConversionRate() { return conversionRate; }
    public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }
    public double getPaymentSuccessRate() { return paymentSuccessRate; }
    public void setPaymentSuccessRate(double paymentSuccessRate) { this.paymentSuccessRate = paymentSuccessRate; }
    public double getUpsellConversionRate() { return upsellConversionRate; }
    public void setUpsellConversionRate(double upsellConversionRate) { this.upsellConversionRate = upsellConversionRate; }
    public List<RevenueInsightDto> getInsights() { return insights; }
    public void setInsights(List<RevenueInsightDto> insights) { this.insights = insights; }
}