package com.litethinking.company.domain.port.out;

import com.litethinking.company.domain.model.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepositoryPort {

    List<Company> findAll();

    Optional<Company> findById(UUID id);

    boolean existsByNit(String nit);

    boolean existsByNitAndIdNot(String nit, UUID id);

    Company save(Company company);
}
