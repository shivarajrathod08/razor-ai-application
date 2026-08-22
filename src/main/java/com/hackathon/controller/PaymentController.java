package com.hackathon.controller;


import com.hackathon.dto.*;
import com.hackathon.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final RazorpayService razorpayService;

    public PaymentController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/verify")
    public ApiResponse<PaymentVerifyResponse> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        PaymentVerifyResponse response = razorpayService.verifyPayment(request);
        return ApiResponse.ok(response.getMessage(), response);
    }
}