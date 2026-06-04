package com.litethinking.product.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litethinking.product.domain.port.in.ProductUseCase;
import com.litethinking.product.infrastructure.config.SecurityConfig;
import com.litethinking.product.infrastructure.web.dto.ProductPriceDTO;
import com.litethinking.product.infrastructure.web.dto.ProductRequestDTO;
import com.litethinking.product.infrastructure.web.dto.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

    private UUID productId;
    private ProductRequestDTO validRequest;
    private ProductResponseDTO productResponse;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        validRequest = new ProductRequestDTO();
        validRequest.setCode("P001");
        validRequest.setName("Test Product");
        validRequest.setCompanyId(UUID.randomUUID());
        validRequest.setPrices(List.of(new ProductPriceDTO("COP", BigDecimal.valueOf(1000))));
        validRequest.setCategoryIds(new ArrayList<>());

        productResponse = new ProductResponseDTO();
        productResponse.setId(productId);
        productResponse.setCode("P001");
        productResponse.setName("Test Product");
        productResponse.setPrices(new ArrayList<>());
        productResponse.setCategories(new ArrayList<>());
    }

    @Test
    void getProducts_withAnyRole_returns200() throws Exception {
        // given
        when(productUseCase.findAll()).thenReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/products")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_withAdminRole_returns201() throws Exception {
        // given
        when(productUseCase.create(any(ProductRequestDTO.class))).thenReturn(productResponse);

        // when / then
        mockMvc.perform(post("/api/products")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void createProduct_withExternalRole_returns403() throws Exception {
        // given — no mock needed, controller rejects before reaching use case

        // when / then
        mockMvc.perform(post("/api/products")
                        .header("X-User-Role", "EXTERNAL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProduct_withAdminRole_returns200() throws Exception {
        // given
        when(productUseCase.update(eq(productId), any(ProductRequestDTO.class))).thenReturn(productResponse);

        // when / then
        mockMvc.perform(put("/api/products/{id}", productId)
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_withAdminRole_returns204() throws Exception {
        // given
        doNothing().when(productUseCase).delete(productId);

        // when / then
        mockMvc.perform(delete("/api/products/{id}", productId)
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_withExternalRole_returns403() throws Exception {
        // given — no mock needed, controller rejects before reaching use case

        // when / then
        mockMvc.perform(delete("/api/products/{id}", productId)
                        .header("X-User-Role", "EXTERNAL"))
                .andExpect(status().isForbidden());
    }
}
