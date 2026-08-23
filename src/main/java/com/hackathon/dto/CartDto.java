package com.hackathon.dto;



import com.hackathon.model.Cart;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartDto {
    private Long id;
    private String sessionId;
    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal calculatedTotal = BigDecimal.ZERO;
    private String currency = "INR";
    private int totalItemCount = 0;

    public CartDto() {}

    public static CartDto fromEntity(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setSessionId(cart.getSessionId());
        dto.setCurrency(cart.getCurrency());
        dto.setCalculatedTotal(cart.getCalculatedTotal());
        if (cart.getItems() != null) {
            dto.setItems(cart.getItems().stream().map(CartItemDto::fromEntity).collect(Collectors.toList()));
            dto.setTotalItemCount(cart.getItems().stream().mapToInt(item -> item.getQuantity()).sum());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }
    public BigDecimal getCalculatedTotal() { return calculatedTotal; }
    public void setCalculatedTotal(BigDecimal calculatedTotal) { this.calculatedTotal = calculatedTotal; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public int getTotalItemCount() { return totalItemCount; }
    public void setTotalItemCount(int totalItemCount) { this.totalItemCount = totalItemCount; }
}