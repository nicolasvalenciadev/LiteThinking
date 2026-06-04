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

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "category_product")
@SQLRestriction("deleted = false")
public class CategoryProductEntity extends AuditableEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    private ProductEntity product;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
