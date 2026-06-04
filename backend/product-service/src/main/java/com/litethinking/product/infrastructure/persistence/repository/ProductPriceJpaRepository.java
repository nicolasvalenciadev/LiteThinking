package com.litethinking.product.infrastructure.persistence.repository;

import com.litethinking.product.infrastructure.persistence.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductPriceJpaRepository extends JpaRepository<ProductPriceEntity, UUID> {
    List<ProductPriceEntity> findByProductId(UUID productId);

    @Modifying
    @Query("UPDATE ProductPriceEntity p SET p.deleted = true WHERE p.productId = :productId")
    void softDeleteByProductId(@Param("productId") UUID productId);
}
