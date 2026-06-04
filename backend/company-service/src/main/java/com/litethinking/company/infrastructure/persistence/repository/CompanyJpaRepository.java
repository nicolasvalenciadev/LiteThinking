package com.litethinking.company.infrastructure.persistence.repository;

import com.litethinking.company.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {

    boolean existsByNit(String nit);

    boolean existsByNitAndIdNot(String nit, UUID id);
}
