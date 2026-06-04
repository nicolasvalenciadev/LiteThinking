package com.litethinking.product.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "category_product")
@SQLRestriction("deleted = false")
public class CategoryProductEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "id_category", nullable = false)
    private UUID categoryId;

    @Column(name = "id_product", nullable = false)
    private UUID productId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
}
