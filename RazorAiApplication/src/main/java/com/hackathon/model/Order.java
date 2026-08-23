package com.hackathon.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hackathon.model.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_number", columnList = "orderNumber"),
        @Index(name = "idx_order_session", columnList = "sessionId"),
        @Index(name = "idx_order_status", columnList = "status")
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String sessionId;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.DRAFT;

    private boolean customerConfirmed = false;
    private LocalDateTime customerConfirmedAt;

    private boolean safetyPassed = false;

    @Column(length = 2000)
    private String safetyCheckSummary;

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Order() {}

    public Order(String orderNumber, String sessionId, BigDecimal totalAmount, String currency, String idempotencyKey) {
        this.orderNumber = orderNumber;
        this.sessionId = sessionId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.idempotencyKey = idempotencyKey;
        this.status = OrderStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}