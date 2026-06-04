package com.litethinking.company.infrastructure.mapper;

import com.litethinking.company.domain.model.Company;
import com.litethinking.company.infrastructure.persistence.entity.CompanyEntity;
import com.litethinking.company.infrastructure.web.dto.CompanyRequestDTO;
import com.litethinking.company.infrastructure.web.dto.CompanyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company toDomain(CompanyEntity entity) {
        Company company = new Company();
        company.setId(entity.getId());
        company.setName(entity.getName());
        company.setNit(entity.getNit());
        company.setAddress(entity.getAddress());
        company.setTelephone(entity.getTelephone());
        company.setCreatedDate(entity.getCreatedDate());
        company.setLastUpdate(entity.getLastUpdate());
        company.setDeleted(entity.isDeleted());
        return company;
    }

    public CompanyEntity toEntity(Company company) {
        CompanyEntity entity = new CompanyEntity();
        entity.setId(company.getId());
        entity.setName(company.getName());
        entity.setNit(company.getNit());
        entity.setAddress(company.getAddress());
        entity.setTelephone(company.getTelephone());
        entity.setCreatedDate(company.getCreatedDate());
        entity.setLastUpdate(company.getLastUpdate());
        entity.setDeleted(company.isDeleted());
        return entity;
    }

    public Company toDomain(CompanyRequestDTO dto) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setNit(dto.getNit());
        company.setAddress(dto.getAddress());
        company.setTelephone(dto.getTelephone());
        return company;
    }

    public CompanyResponseDTO toResponseDTO(Company company) {
        return CompanyResponseDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .nit(company.getNit())
                .address(company.getAddress())
                .telephone(company.getTelephone())
                .createdDate(company.getCreatedDate())
                .lastUpdate(company.getLastUpdate())
                .build();
    }
}
