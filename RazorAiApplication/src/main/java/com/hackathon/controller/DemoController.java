package com.hackathon.controller;

import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.PriceTamperDemoRequest;
import com.hackathon.dto.PriceTamperDemoResponse;
import com.hackathon.service.CartService;
import com.hackathon.service.DemoScenarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    private final DemoScenarioService demoService;
    private final CartService cartService;

    public DemoController(DemoScenarioService demoService, CartService cartService) {
        this.demoService = demoService;
        this.cartService = cartService;
    }

    @PostMapping("/load-cart")
    public ApiResponse<Map<String, Object>> loadDemoCart(@RequestParam String sessionId) {
        return ApiResponse.ok(demoService.loadDemoCart(sessionId));
    }

    @PostMapping("/reset")
    public ApiResponse<Map<String, Object>> resetDemo() {
        return ApiResponse.ok(demoService.resetDemoState());
    }

    @PostMapping("/price-tamper")
    public ApiResponse<PriceTamperDemoResponse> testPriceTamper(@Valid @RequestBody PriceTamperDemoRequest req) {
        return ApiResponse.ok(cartService.runPriceTamperDemo(req.getSessionId(), req.getProductId(), req.getTamperedPrice()));
    }
}