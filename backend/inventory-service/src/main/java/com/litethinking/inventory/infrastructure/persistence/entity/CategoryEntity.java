package com.litethinking.inventory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "categories")
@SQLRestriction("deleted = false")
public class CategoryEntity extends AuditableEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
