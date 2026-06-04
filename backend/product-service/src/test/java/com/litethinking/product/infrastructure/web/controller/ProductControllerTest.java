package com.litethinking.product.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litethinking.product.domain.port.in.ProductUseCase;
import com.litethinking.product.infrastructure.config.SecurityConfig;
import com.litethinking.product.infrastructure.web.dto.ProductPriceDTO;
import com.litethinking.product.infrastructure.web.dto.ProductRequestDTO;
import com.litethinking.product.infrastructure.web.dto.ProductResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductUseCase productUseCase;

    @Test
    void getProducts_returns200() throws Exception {
        when(productUseCase.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/products")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_withAdminRole_returns201() throws Exception {
        ProductRequestDTO request = buildValidRequest();
        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(UUID.randomUUID());
        response.setCode("P001");
        response.setName("Test Product");
        response.setPrices(new ArrayList<>());
        response.setCategories(new ArrayList<>());

        when(productUseCase.create(any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createProduct_withExternalRole_returns403() throws Exception {
        ProductRequestDTO request = buildValidRequest();

        mockMvc.perform(post("/api/products")
                        .header("X-User-Role", "EXTERNAL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_withExternalRole_returns403() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/products/{id}", id)
                        .header("X-User-Role", "EXTERNAL"))
                .andExpect(status().isForbidden());
    }

    private ProductRequestDTO buildValidRequest() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setCode("P001");
        request.setName("Test Product");
        request.setCompanyId(UUID.randomUUID());
        request.setPrices(List.of(new ProductPriceDTO("COP", BigDecimal.valueOf(1000))));
        request.setCategoryIds(new ArrayList<>());
        return request;
    }
}
