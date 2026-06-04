package com.litethinking.company.infrastructure.persistence.adapter;

import com.litethinking.company.domain.model.Company;
import com.litethinking.company.domain.port.out.CompanyRepositoryPort;
import com.litethinking.company.infrastructure.mapper.CompanyMapper;
import com.litethinking.company.infrastructure.persistence.entity.CompanyEntity;
import com.litethinking.company.infrastructure.persistence.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final CompanyJpaRepository jpaRepository;
    private final CompanyMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        try {
            return jpaRepository.findAll().stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al consultar el listado de empresas", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findById(UUID id) {
        try {
            return jpaRepository.findById(id).map(mapper::toDomain);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al consultar la empresa con id: " + id, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNit(String nit) {
        try {
            return jpaRepository.existsByNit(nit);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al verificar existencia del NIT: " + nit, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNitAndIdNot(String nit, UUID id) {
        try {
            return jpaRepository.existsByNitAndIdNot(nit, id);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al verificar unicidad del NIT: " + nit, ex);
        }
    }

    @Override
    public Company save(Company company) {
        try {
            CompanyEntity entity = mapper.toEntity(company);
            return mapper.toDomain(jpaRepository.save(entity));
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al guardar la empresa", ex);
        }
    }
}
