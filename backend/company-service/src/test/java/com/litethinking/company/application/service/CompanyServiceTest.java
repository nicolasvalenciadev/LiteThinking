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

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    void findAll_returnsAllCompanies() {
        // given
        List<Company> companies = List.of(buildCompany("Empresa A", "111"), buildCompany("Empresa B", "222"));
        when(repositoryPort.findAll()).thenReturn(companies);

        // when
        List<Company> result = companyService.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(repositoryPort).findAll();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_returnsCompany_whenExists() {
        // given
        UUID id = UUID.randomUUID();
        Company company = buildCompany("Empresa A", "111");
        company.setId(id);
        when(repositoryPort.findById(id)).thenReturn(Optional.of(company));

        // when
        Company result = companyService.findById(id);

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNit()).isEqualTo("111");
    }

    @Test
    void findById_throwsCompanyNotFoundException_whenNotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.findById(id))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsCompany_whenNitIsNew() {
        // given
        Company company = buildCompany("Nueva Empresa", "999");
        when(repositoryPort.existsByNit("999")).thenReturn(false);
        when(repositoryPort.save(company)).thenReturn(company);

        // when
        Company result = companyService.create(company);

        // then
        assertThat(result.getNit()).isEqualTo("999");
        verify(repositoryPort).save(company);
    }

    @Test
    void create_throwsCompanyAlreadyExistsException_whenNitExists() {
        // given
        Company company = buildCompany("Nueva Empresa", "999");
        when(repositoryPort.existsByNit("999")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> companyService.create(company))
                .isInstanceOf(CompanyAlreadyExistsException.class)
                .hasMessageContaining("999");
        verify(repositoryPort, never()).save(any());
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    void update_updatesAndReturnsCompany_whenValid() {
        // given
        UUID id = UUID.randomUUID();
        Company existing = buildCompany("Empresa Original", "111");
        existing.setId(id);
        Company updates = buildCompany("Empresa Actualizada", "222");
        when(repositoryPort.findById(id)).thenReturn(Optional.of(existing));
        when(repositoryPort.existsByNitAndIdNot("222", id)).thenReturn(false);
        when(repositoryPort.save(any(Company.class))).thenReturn(existing);

        // when
        Company result = companyService.update(id, updates);

        // then
        assertThat(result.getName()).isEqualTo("Empresa Actualizada");
        assertThat(result.getNit()).isEqualTo("222");
        verify(repositoryPort).save(existing);
    }

    @Test
    void update_throwsCompanyNotFoundException_whenNotFound() {
        // given
        UUID id = UUID.randomUUID();
        Company updates = buildCompany("Actualizada", "555");
        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.update(id, updates))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(repositoryPort, never()).save(any());
    }

    @Test
    void update_throwsCompanyAlreadyExistsException_whenNitTakenByOtherCompany() {
        // given
        UUID id = UUID.randomUUID();
        Company existing = buildCompany("Empresa Original", "111");
        existing.setId(id);
        Company updates = buildCompany("Empresa Actualizada", "999");
        when(repositoryPort.findById(id)).thenReturn(Optional.of(existing));
        when(repositoryPort.existsByNitAndIdNot("999", id)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> companyService.update(id, updates))
                .isInstanceOf(CompanyAlreadyExistsException.class)
                .hasMessageContaining("999");
        verify(repositoryPort, never()).save(any());
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_performsSoftDelete_whenCompanyExists() {
        // given
        UUID id = UUID.randomUUID();
        Company company = buildCompany("Empresa A", "111");
        company.setId(id);
        company.setDeleted(false);
        when(repositoryPort.findById(id)).thenReturn(Optional.of(company));
        when(repositoryPort.save(any(Company.class))).thenReturn(company);

        // when
        companyService.delete(id);

        // then
        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(repositoryPort).save(captor.capture());
        assertThat(captor.getValue().isDeleted()).isTrue();
    }

    @Test
    void delete_throwsCompanyNotFoundException_whenNotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.delete(id))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(repositoryPort, never()).save(any());
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Company buildCompany(String name, String nit) {
        Company company = new Company();
        company.setName(name);
        company.setNit(nit);
        company.setDeleted(false);
        return company;
    }
}
