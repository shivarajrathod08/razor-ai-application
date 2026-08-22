package com.hackathon.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String storeName;

    private String currency = "INR";
    private boolean testModeEnabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Merchant() {}

    public Merchant(String storeName) {
        this.storeName = storeName;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isTestModeEnabled() { return testModeEnabled; }
    public void setTestModeEnabled(boolean testModeEnabled) { this.testModeEnabled = testModeEnabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}