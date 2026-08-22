package com.hackathon.controller;


import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.CheckoutConfirmRequest;
import com.hackathon.dto.CheckoutProposalDto;
import com.hackathon.dto.PaymentOrderResponse;
import com.hackathon.model.Order;
import com.hackathon.service.OrderService;
import com.hackathon.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final OrderService orderService;
    private final RazorpayService razorpayService;

    public CheckoutController(OrderService orderService, RazorpayService razorpayService) {
        this.orderService = orderService;
        this.razorpayService = razorpayService;
    }

    @PostMapping("/propose")
    public ApiResponse<CheckoutProposalDto> proposeCheckout(@RequestParam String sessionId) {
        return ApiResponse.ok("Checkout proposal generated. Customer confirmation required.", orderService.createCheckoutProposal(sessionId));
    }

    @PostMapping("/confirm")
    public ApiResponse<PaymentOrderResponse> confirmAndInitiatePayment(@Valid @RequestBody CheckoutConfirmRequest request) {
        // Step 1: Enforce customer confirmation and backend safety checks
        Order confirmedOrder = orderService.confirmAndAuthorizeOrder(request.getSessionId(), request.getIdempotencyKey(), request.isCustomerConfirmed());

        // Step 2: Create Razorpay Test Order
        PaymentOrderResponse paymentResponse = razorpayService.createPaymentOrder(confirmedOrder, request.isSimulateFailure());

        return ApiResponse.ok("Payment authorized and Razorpay order created", paymentResponse);
    }
}