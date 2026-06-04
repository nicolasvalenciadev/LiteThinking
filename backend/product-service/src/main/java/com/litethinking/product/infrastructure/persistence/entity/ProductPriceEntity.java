package com.litethinking.product.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_prices")
@SQLRestriction("deleted = false")
public class ProductPriceEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
