package com.hackathon.controller;


import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.OrderDto;
import com.hackathon.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ApiResponse<List<OrderDto>> getAllOrders() {
        return ApiResponse.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderNumber}")
    public ApiResponse<OrderDto> getOrder(@PathVariable String orderNumber) {
        return ApiResponse.ok(orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping("/session/{sessionId}")
    public ApiResponse<List<OrderDto>> getOrdersBySession(@PathVariable String sessionId) {
        return ApiResponse.ok(orderService.getOrdersBySession(sessionId));
    }
}