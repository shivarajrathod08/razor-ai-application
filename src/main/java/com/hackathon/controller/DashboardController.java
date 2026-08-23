package com.hackathon.controller;

import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.DashboardMetricsDto;
import com.hackathon.service.DashboardAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardAnalyticsService dashboardService;

    public DashboardController(DashboardAnalyticsService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    public ApiResponse<DashboardMetricsDto> getMetrics() {
        return ApiResponse.ok(dashboardService.getMetrics());
    }
}