package com.hackathon.dto;


import com.hackathon.model.Product;
import java.math.BigDecimal;

public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private String currency;
    private int stock;
    private String imageUrl;
    private String tags;
    private boolean active;
    private String upsellProductIds;
    private String upsellRationale;

    public ProductDto() {}

    public static ProductDto fromEntity(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setCategory(p.getCategory());
        dto.setPrice(p.getPrice());
        dto.setCurrency(p.getCurrency());
        dto.setStock(p.getStock());
        dto.setImageUrl(p.getImageUrl());
        dto.setTags(p.getTags());
        dto.setActive(p.isActive());
        dto.setUpsellProductIds(p.getUpsellProductIds());
        dto.setUpsellRationale(p.getUpsellRationale());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getUpsellProductIds() { return upsellProductIds; }
    public void setUpsellProductIds(String upsellProductIds) { this.upsellProductIds = upsellProductIds; }
    public String getUpsellRationale() { return upsellRationale; }
    public void setUpsellRationale(String upsellRationale) { this.upsellRationale = upsellRationale; }
}