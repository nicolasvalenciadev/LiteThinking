package com.litethinking.inventory.infrastructure.persistence.repository;

import com.litethinking.inventory.infrastructure.persistence.entity.CategoryProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryProductJpaRepository extends JpaRepository<CategoryProductEntity, UUID> {
}
