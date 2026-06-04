package com.litethinking.product.infrastructure.persistence.repository;

import com.litethinking.product.infrastructure.persistence.entity.CategoryProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryProductJpaRepository extends JpaRepository<CategoryProductEntity, UUID> {

    @Query("SELECT cp.categoryId FROM CategoryProductEntity cp WHERE cp.productId = :productId")
    List<UUID> findCategoryIdsByProductId(@Param("productId") UUID productId);

    @Modifying
    @Query("UPDATE CategoryProductEntity cp SET cp.deleted = true WHERE cp.productId = :productId")
    void softDeleteByProductId(@Param("productId") UUID productId);
}
