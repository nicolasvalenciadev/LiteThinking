package com.litethinking.inventory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_prices")
@SQLRestriction("deleted = false")
public class ProductPriceEntity extends AuditableEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
