package com.hackathon.controller;



import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.AuditEventDto;
import com.hackathon.service.AuditTrailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditTrailService auditService;

    public AuditController(AuditTrailService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ApiResponse<List<AuditEventDto>> getAllEvents() {
        return ApiResponse.ok(auditService.getAllEvents());
    }

    @GetMapping("/session/{sessionId}")
    public ApiResponse<List<AuditEventDto>> getEventsBySession(@PathVariable String sessionId) {
        return ApiResponse.ok(auditService.getEventsBySession(sessionId));
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<List<AuditEventDto>> getEventsByOrder(@PathVariable String orderId) {
        return ApiResponse.ok(auditService.getEventsByOrder(orderId));
    }
}