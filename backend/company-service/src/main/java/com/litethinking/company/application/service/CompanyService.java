package com.litethinking.company.application.service;

import com.litethinking.company.domain.exception.CompanyAlreadyExistsException;
import com.litethinking.company.domain.exception.CompanyNotFoundException;
import com.litethinking.company.domain.model.Company;
import com.litethinking.company.domain.port.in.CompanyUseCase;
import com.litethinking.company.domain.port.out.CompanyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService implements CompanyUseCase {

    private final CompanyRepositoryPort repositoryPort;

    public CompanyService(CompanyRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return repositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Company findById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Empresa no encontrada con id: " + id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Company create(Company company) {
        if (repositoryPort.existsByNit(company.getNit())) {
            throw new CompanyAlreadyExistsException(
                    "Ya existe una empresa registrada con el NIT: " + company.getNit());
        }
        return repositoryPort.save(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Company update(UUID id, Company updates) {
        Company existing = repositoryPort.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Empresa no encontrada con id: " + id));

        if (repositoryPort.existsByNitAndIdNot(updates.getNit(), id)) {
            throw new CompanyAlreadyExistsException(
                    "El NIT " + updates.getNit() + " ya está en uso por otra empresa");
        }

        existing.setName(updates.getName());
        existing.setNit(updates.getNit());
        existing.setAddress(updates.getAddress());
        existing.setTelephone(updates.getTelephone());

        return repositoryPort.save(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID id) {
        Company company = repositoryPort.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Empresa no encontrada con id: " + id));
        company.setDeleted(true);
        repositoryPort.save(company);
    }
}
