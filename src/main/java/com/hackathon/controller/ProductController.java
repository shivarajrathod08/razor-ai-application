package com.hackathon.controller;

import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.ProductDto;
import com.hackathon.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductDto>> getAllProducts() {
        return ApiResponse.ok(productService.getAllActiveProducts());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDto> getProductById(@PathVariable Long id) {
        return ApiResponse.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductDto>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ApiResponse.ok(productService.searchProducts(query, maxPrice));
    }

    @GetMapping("/upsell/{id}")
    public ApiResponse<List<ProductDto>> getUpsellProducts(@PathVariable Long id) {
        return ApiResponse.ok(productService.getUpsellRecommendations(id));
    }
}