package com.litethinking.company.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litethinking.company.domain.model.Company;
import com.litethinking.company.domain.port.in.CompanyUseCase;
import com.litethinking.company.infrastructure.config.SecurityConfig;
import com.litethinking.company.infrastructure.mapper.CompanyMapper;
import com.litethinking.company.infrastructure.web.dto.CompanyRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyController.class)
@Import({SecurityConfig.class, CompanyMapper.class})
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyUseCase companyUseCase;

    // ─── GET /api/companies ───────────────────────────────────────────────────

    @Test
    void findAll_returns200WithCompanyList() throws Exception {
        // given
        Company company = buildCompany("Empresa A", "111-1");
        when(companyUseCase.findAll()).thenReturn(List.of(company));

        // when / then
        mockMvc.perform(get("/api/companies")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Empresa A"))
                .andExpect(jsonPath("$[0].nit").value("111-1"));
    }

    // ─── POST /api/companies ──────────────────────────────────────────────────

    @Test
    void create_returns201_whenAdminRole() throws Exception {
        // given
        Company created = buildCompany("Empresa B", "222-2");
        created.setId(UUID.randomUUID());
        when(companyUseCase.create(any(Company.class))).thenReturn(created);
        CompanyRequestDTO request = new CompanyRequestDTO("Empresa B", "222-2", null, null);

        // when / then
        mockMvc.perform(post("/api/companies")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nit").value("222-2"));
    }

    @Test
    void create_returns403_whenExternalRole() throws Exception {
        // given
        CompanyRequestDTO request = new CompanyRequestDTO("Empresa C", "333-3", null, null);

        // when / then
        mockMvc.perform(post("/api/companies")
                        .header("X-User-Role", "EXTERNAL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ─── PUT /api/companies/{id} ──────────────────────────────────────────────

    @Test
    void update_returns200_whenAdminRole() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        Company updated = buildCompany("Empresa Actualizada", "444-4");
        updated.setId(id);
        when(companyUseCase.update(eq(id), any(Company.class))).thenReturn(updated);
        CompanyRequestDTO request = new CompanyRequestDTO("Empresa Actualizada", "444-4", null, null);

        // when / then
        mockMvc.perform(put("/api/companies/" + id)
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Empresa Actualizada"));
    }

    @Test
    void update_returns403_whenExternalRole() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        CompanyRequestDTO request = new CompanyRequestDTO("Empresa X", "555-5", null, null);

        // when / then
        mockMvc.perform(put("/api/companies/" + id)
                        .header("X-User-Role", "EXTERNAL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ─── DELETE /api/companies/{id} ───────────────────────────────────────────

    @Test
    void delete_returns204_whenAdminRole() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        doNothing().when(companyUseCase).delete(id);

        // when / then
        mockMvc.perform(delete("/api/companies/" + id)
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns403_whenExternalRole() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        // when / then
        mockMvc.perform(delete("/api/companies/" + id)
                        .header("X-User-Role", "EXTERNAL"))
                .andExpect(status().isForbidden());
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Company buildCompany(String name, String nit) {
        Company company = new Company();
        company.setName(name);
        company.setNit(nit);
        return company;
    }
}
