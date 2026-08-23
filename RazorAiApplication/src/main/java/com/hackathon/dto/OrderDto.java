package com.hackathon.dto;



import com.hackathon.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDto {
    private Long id;
    private String orderNumber;
    private String sessionId;
    private List<OrderItemDto> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private boolean customerConfirmed;
    private LocalDateTime customerConfirmedAt;
    private boolean safetyPassed;
    private String safetyCheckSummary;
    private String idempotencyKey;
    private LocalDateTime createdAt;

    public OrderDto() {}

    public static OrderDto fromEntity(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setSessionId(order.getSessionId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCurrency(order.getCurrency());
        dto.setStatus(order.getStatus().name());
        dto.setCustomerConfirmed(order.isCustomerConfirmed());
        dto.setCustomerConfirmedAt(order.getCustomerConfirmedAt());
        dto.setSafetyPassed(order.isSafetyPassed());
        dto.setSafetyCheckSummary(order.getSafetyCheckSummary());
        dto.setIdempotencyKey(order.getIdempotencyKey());
        dto.setCreatedAt(order.getCreatedAt());
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(OrderItemDto::fromEntity).collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isCustomerConfirmed() { return customerConfirmed; }
    public void setCustomerConfirmed(boolean customerConfirmed) { this.customerConfirmed = customerConfirmed; }
    public LocalDateTime getCustomerConfirmedAt() { return customerConfirmedAt; }
    public void setCustomerConfirmedAt(LocalDateTime customerConfirmedAt) { this.customerConfirmedAt = customerConfirmedAt; }
    public boolean isSafetyPassed() { return safetyPassed; }
    public void setSafetyPassed(boolean safetyPassed) { this.safetyPassed = safetyPassed; }
    public String getSafetyCheckSummary() { return safetyCheckSummary; }
    public void setSafetyCheckSummary(String safetyCheckSummary) { this.safetyCheckSummary = safetyCheckSummary; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}