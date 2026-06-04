package com.litethinking.inventory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products")
@SQLRestriction("deleted = false")
public class ProductEntity extends AuditableEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<ProductPriceEntity> prices = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<CategoryProductEntity> categoryProducts = new LinkedHashSet<>();
}
