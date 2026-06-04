package com.litethinking.product.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductPrice {

    private UUID id;
    private UUID productId;
    private String currency;
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private boolean deleted;

    public ProductPrice() {}

    public ProductPrice(UUID id, UUID productId, String currency, BigDecimal price,
                        LocalDateTime createdDate, LocalDateTime lastUpdate, boolean deleted) {
        this.id = id;
        this.productId = productId;
        this.currency = currency;
        this.price = price;
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
        this.deleted = deleted;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
