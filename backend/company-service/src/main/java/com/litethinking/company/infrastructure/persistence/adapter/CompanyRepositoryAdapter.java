package com.litethinking.company.infrastructure.persistence.adapter;

import com.litethinking.company.domain.model.Company;
import com.litethinking.company.domain.port.out.CompanyRepositoryPort;
import com.litethinking.company.infrastructure.mapper.CompanyMapper;
import com.litethinking.company.infrastructure.persistence.entity.CompanyEntity;
import com.litethinking.company.infrastructure.persistence.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final CompanyJpaRepository jpaRepository;
    private final CompanyMapper mapper;

    @Override
    public List<Company> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNit(String nit) {
        return jpaRepository.existsByNit(nit);
    }

    @Override
    public boolean existsByNitAndIdNot(String nit, UUID id) {
        return jpaRepository.existsByNitAndIdNot(nit, id);
    }

    @Override
    public Company save(Company company) {
        CompanyEntity entity = mapper.toEntity(company);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
