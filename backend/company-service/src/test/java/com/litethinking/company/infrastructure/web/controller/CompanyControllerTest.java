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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void getCompanies_returns200() throws Exception {
        Company company = buildCompany("Empresa A", "111-1");
        when(companyUseCase.findAll()).thenReturn(List.of(company));

        mockMvc.perform(get("/api/companies")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Empresa A"));
    }

    @Test
    void createCompany_withAdminRole_returns201() throws Exception {
        Company company = buildCompany("Empresa B", "222-2");
        company.setId(UUID.randomUUID());
        when(companyUseCase.create(any(Company.class))).thenReturn(company);

        CompanyRequestDTO request = new CompanyRequestDTO("Empresa B", "222-2", null, null);

        mockMvc.perform(post("/api/companies")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nit").value("222-2"));
    }

    @Test
    void createCompany_withExternalRole_returns403() throws Exception {
        CompanyRequestDTO request = new CompanyRequestDTO("Empresa C", "333-3", null, null);

        mockMvc.perform(post("/api/companies")
                        .header("X-User-Role", "EXTERNAL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCompany_withExternalRole_returns403() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/companies/" + id)
                        .header("X-User-Role", "EXTERNAL"))
                .andExpect(status().isForbidden());
    }

    private Company buildCompany(String name, String nit) {
        Company company = new Company();
        company.setName(name);
        company.setNit(nit);
        return company;
    }
}
