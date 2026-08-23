package com.hackathon.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AddToCartRequest {
    @NotNull
    private Long productId;

    @Min(1)
    private int quantity = 1;

    private boolean isUpsell = false;

    // Client/LLM may try to send a price; backend MUST verify and overwrite from DB
    private BigDecimal untrustedPrice;

    public AddToCartRequest() {}

    public AddToCartRequest(Long productId, int quantity, boolean isUpsell) {
        this.productId = productId;
        this.quantity = quantity;
        this.isUpsell = isUpsell;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isUpsell() { return isUpsell; }
    public void setUpsell(boolean upsell) { isUpsell = upsell; }
    public BigDecimal getUntrustedPrice() { return untrustedPrice; }
    public void setUntrustedPrice(BigDecimal untrustedPrice) { this.untrustedPrice = untrustedPrice; }
}