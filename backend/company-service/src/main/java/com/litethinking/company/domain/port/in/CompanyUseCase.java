package com.litethinking.company.domain.port.in;

import com.litethinking.company.domain.model.Company;

import java.util.List;
import java.util.UUID;

public interface CompanyUseCase {

    List<Company> findAll();

    Company findById(UUID id);

    Company create(Company company);

    Company update(UUID id, Company company);

    void delete(UUID id);
}
