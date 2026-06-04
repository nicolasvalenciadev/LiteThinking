package com.litethinking.company.application.service;

import com.litethinking.company.domain.exception.CompanyAlreadyExistsException;
import com.litethinking.company.domain.exception.CompanyNotFoundException;
import com.litethinking.company.domain.model.Company;
import com.litethinking.company.domain.port.out.CompanyRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepositoryPort repositoryPort;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void findAll_returnsListOfCompanies() {
        List<Company> companies = List.of(buildCompany("Empresa A", "111"), buildCompany("Empresa B", "222"));
        when(repositoryPort.findAll()).thenReturn(companies);

        List<Company> result = companyService.findAll();

        assertThat(result).hasSize(2);
        verify(repositoryPort).findAll();
    }

    @Test
    void findById_returnsCompany_whenExists() {
        UUID id = UUID.randomUUID();
        Company company = buildCompany("Empresa A", "111");
        company.setId(id);
        when(repositoryPort.findById(id)).thenReturn(Optional.of(company));

        Company result = companyService.findById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNit()).isEqualTo("111");
    }

    @Test
    void findById_throwsCompanyNotFoundException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findById(id))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void create_savesAndReturnsCompany() {
        Company company = buildCompany("Nueva Empresa", "999");
        when(repositoryPort.existsByNit("999")).thenReturn(false);
        when(repositoryPort.save(company)).thenReturn(company);

        Company result = companyService.create(company);

        assertThat(result.getNit()).isEqualTo("999");
        verify(repositoryPort).save(company);
    }

    @Test
    void create_throwsCompanyAlreadyExistsException_whenNitExists() {
        Company company = buildCompany("Nueva Empresa", "999");
        when(repositoryPort.existsByNit("999")).thenReturn(true);

        assertThatThrownBy(() -> companyService.create(company))
                .isInstanceOf(CompanyAlreadyExistsException.class)
                .hasMessageContaining("999");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void update_throwsCompanyNotFoundException_whenNotFound() {
        UUID id = UUID.randomUUID();
        Company updates = buildCompany("Actualizada", "555");
        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.update(id, updates))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void delete_performsSoftDelete() {
        UUID id = UUID.randomUUID();
        Company company = buildCompany("Empresa A", "111");
        company.setId(id);
        company.setDeleted(false);
        when(repositoryPort.findById(id)).thenReturn(Optional.of(company));
        when(repositoryPort.save(any(Company.class))).thenReturn(company);

        companyService.delete(id);

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(repositoryPort).save(captor.capture());
        assertThat(captor.getValue().isDeleted()).isTrue();
    }

    private Company buildCompany(String name, String nit) {
        Company company = new Company();
        company.setName(name);
        company.setNit(nit);
        company.setDeleted(false);
        return company;
    }
}
