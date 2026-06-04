package com.litethinking.inventory.infrastructure.persistence.repository;

import com.litethinking.inventory.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT DISTINCT p FROM ProductEntity p LEFT JOIN FETCH p.prices")
    List<ProductEntity> findAllWithPrices();

    @Query("SELECT DISTINCT p FROM ProductEntity p LEFT JOIN FETCH p.categoryProducts cp LEFT JOIN FETCH cp.category")
    List<ProductEntity> findAllWithCategories();
}
