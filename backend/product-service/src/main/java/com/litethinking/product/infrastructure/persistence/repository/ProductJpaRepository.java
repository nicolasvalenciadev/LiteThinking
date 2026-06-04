package com.litethinking.product.infrastructure.persistence.repository;

import com.litethinking.product.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findByCode(String code);
    List<ProductEntity> findByCompanyId(UUID companyId);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.deleted = true WHERE p.id = :id")
    void softDeleteById(@Param("id") UUID id);
}
