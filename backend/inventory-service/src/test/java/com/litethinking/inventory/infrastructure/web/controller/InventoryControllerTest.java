package com.litethinking.inventory.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litethinking.inventory.domain.model.InventoryItem;
import com.litethinking.inventory.domain.model.ProductPrice;
import com.litethinking.inventory.domain.port.in.InventoryUseCase;
import com.litethinking.inventory.infrastructure.web.dto.SendEmailRequestDTO;
import com.litethinking.inventory.infrastructure.config.SecurityConfig;
import com.litethinking.inventory.infrastructure.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InventoryController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryUseCase inventoryUseCase;

    private InventoryItem sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new InventoryItem();
        sampleItem.setProductId(UUID.randomUUID());
        sampleItem.setProductCode("PROD-001");
        sampleItem.setProductName("Producto Test");
        sampleItem.setProductDescription("Descripción de prueba");
        sampleItem.setCompanyId(UUID.randomUUID());
        sampleItem.setCompanyName("Empresa Test");
        sampleItem.setCompanyNit("123456789-0");
        sampleItem.setPrices(List.of(new ProductPrice("COP", new BigDecimal("50000"))));
        sampleItem.setCategories(List.of("Electrónica"));
    }

    @Test
    void getInventory_withAdminRole_returns200() throws Exception {
        when(inventoryUseCase.getInventory()).thenReturn(List.of(sampleItem));

        mockMvc.perform(get("/api/inventory")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productCode").value("PROD-001"))
                .andExpect(jsonPath("$[0].companyName").value("Empresa Test"));
    }

    @Test
    void getInventory_withExternalRole_returns403() throws Exception {
        mockMvc.perform(get("/api/inventory")
                        .header("X-User-Role", "EXTERNAL"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void getInventory_withNoRole_returns403() throws Exception {
        mockMvc.perform(get("/api/inventory")
                        .header("X-User-Role", ""))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void generatePdf_withAdminRole_returns200AndPdfContentType() throws Exception {
        byte[] pdfContent = new byte[]{37, 80, 68, 70};
        when(inventoryUseCase.generatePdf()).thenReturn(pdfContent);

        mockMvc.perform(get("/api/inventory/pdf")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"inventario.pdf\""));
    }

    @Test
    void sendEmail_withAdminAndValidEmail_returns200() throws Exception {
        doNothing().when(inventoryUseCase).sendPdfByEmail(anyString());

        SendEmailRequestDTO request = new SendEmailRequestDTO();
        request.setEmail("user@example.com");

        mockMvc.perform(post("/api/inventory/send-email")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "user@example.com")));
    }

    @Test
    void sendEmail_withInvalidEmail_returns400() throws Exception {
        SendEmailRequestDTO request = new SendEmailRequestDTO();
        request.setEmail("not-a-valid-email");

        mockMvc.perform(post("/api/inventory/send-email")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void sendEmail_withBlankEmail_returns400() throws Exception {
        SendEmailRequestDTO request = new SendEmailRequestDTO();
        request.setEmail("");

        mockMvc.perform(post("/api/inventory/send-email")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
