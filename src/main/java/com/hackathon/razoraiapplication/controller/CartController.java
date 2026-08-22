package com.hackathon.razoraiapplication.controller;


import com.hackathon.dto.AddToCartRequest;
import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.CartDto;
import com.hackathon.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<CartDto> getCart(@PathVariable String sessionId) {
        return ApiResponse.ok(cartService.getCartDto(sessionId));
    }

    @PostMapping("/{sessionId}/items")
    public ApiResponse<CartDto> addToCart(
            @PathVariable String sessionId,
            @Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.ok(cartService.addToCart(sessionId, request.getProductId(), request.getQuantity(), request.isUpsell(), request.getUntrustedPrice()));
    }

    @DeleteMapping("/{sessionId}/items/{productId}")
    public ApiResponse<CartDto> removeFromCart(
            @PathVariable String sessionId,
            @PathVariable Long productId) {
        return ApiResponse.ok(cartService.removeFromCart(sessionId, productId));
    }

    @DeleteMapping("/{sessionId}/clear")
    public ApiResponse<Void> clearCart(@PathVariable String sessionId) {
        cartService.clearCart(sessionId);
        return ApiResponse.ok("Cart cleared", null);
    }
}