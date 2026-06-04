package com.litethinking.company.infrastructure.web.controller;

import com.litethinking.company.domain.model.Company;
import com.litethinking.company.domain.port.in.CompanyUseCase;
import com.litethinking.company.infrastructure.mapper.CompanyMapper;
import com.litethinking.company.infrastructure.web.dto.CompanyRequestDTO;
import com.litethinking.company.infrastructure.web.dto.CompanyResponseDTO;
import com.litethinking.company.infrastructure.web.exception.ForbiddenException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyUseCase companyUseCase;
    private final CompanyMapper companyMapper;

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> findAll(
            @RequestHeader("X-User-Role") String userRole) {
        List<CompanyResponseDTO> companies = companyUseCase.findAll().stream()
                .map(companyMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> findById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        Company company = companyUseCase.findById(id);
        return ResponseEntity.ok(companyMapper.toResponseDTO(company));
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> create(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CompanyRequestDTO request) {
        enforceAdminRole(userRole);
        Company company = companyUseCase.create(companyMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(companyMapper.toResponseDTO(company));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> update(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CompanyRequestDTO request) {
        enforceAdminRole(userRole);
        Company company = companyUseCase.update(id, companyMapper.toDomain(request));
        return ResponseEntity.ok(companyMapper.toResponseDTO(company));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        enforceAdminRole(userRole);
        companyUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void enforceAdminRole(String userRole) {
        if (!"ADMIN".equals(userRole)) {
            throw new ForbiddenException(
                    "Acceso denegado: se requiere el rol ADMIN para realizar esta operación");
        }
    }
}
