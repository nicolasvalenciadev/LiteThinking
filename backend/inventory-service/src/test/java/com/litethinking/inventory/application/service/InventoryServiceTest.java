package com.litethinking.inventory.application.service;

import com.litethinking.inventory.domain.exception.EmailDeliveryException;
import com.litethinking.inventory.domain.exception.ReportGenerationException;
import com.litethinking.inventory.domain.model.CompanyInfo;
import com.litethinking.inventory.domain.model.InventoryItem;
import com.litethinking.inventory.domain.port.out.CompanyQueryPort;
import com.litethinking.inventory.domain.port.out.EmailPort;
import com.litethinking.inventory.domain.port.out.ProductQueryPort;
import com.litethinking.inventory.domain.port.out.ReportPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductQueryPort productQueryPort;

    @Mock
    private CompanyQueryPort companyQueryPort;

    @Mock
    private ReportPort reportPort;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private InventoryService inventoryService;

    private UUID companyId;
    private InventoryItem item;
    private CompanyInfo companyInfo;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();

        item = new InventoryItem();
        item.setProductId(UUID.randomUUID());
        item.setProductCode("PROD-001");
        item.setProductName("Producto de prueba");
        item.setCompanyId(companyId);

        companyInfo = new CompanyInfo();
        companyInfo.setId(companyId);
        companyInfo.setName("Empresa Test");
        companyInfo.setNit("123456789-0");
    }

    @Test
    void getInventory_returnsEnrichedListWithCompanyInfo() {
        // given
        when(productQueryPort.fetchProducts()).thenReturn(List.of(item));
        when(companyQueryPort.fetchCompany(companyId)).thenReturn(companyInfo);

        // when
        List<InventoryItem> result = inventoryService.getInventory();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompanyName()).isEqualTo("Empresa Test");
        assertThat(result.get(0).getCompanyNit()).isEqualTo("123456789-0");
        verify(companyQueryPort).fetchCompany(companyId);
    }

    @Test
    void getInventory_handlesCompanyServiceUnavailableGracefully() {
        // given
        CompanyInfo unavailable = new CompanyInfo();
        unavailable.setId(companyId);
        unavailable.setName("Empresa no disponible");
        unavailable.setNit("N/A");

        when(productQueryPort.fetchProducts()).thenReturn(List.of(item));
        when(companyQueryPort.fetchCompany(companyId)).thenReturn(unavailable);

        // when
        List<InventoryItem> result = inventoryService.getInventory();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompanyName()).isEqualTo("Empresa no disponible");
        assertThat(result.get(0).getCompanyNit()).isEqualTo("N/A");
    }

    @Test
    void getInventory_deduplicatesCompanyCallsForSameCompany() {
        // given
        InventoryItem item2 = new InventoryItem();
        item2.setProductId(UUID.randomUUID());
        item2.setProductCode("PROD-002");
        item2.setProductName("Segundo producto");
        item2.setCompanyId(companyId);

        when(productQueryPort.fetchProducts()).thenReturn(List.of(item, item2));
        when(companyQueryPort.fetchCompany(companyId)).thenReturn(companyInfo);

        // when
        List<InventoryItem> result = inventoryService.getInventory();

        // then — only one HTTP call issued despite two products sharing the same companyId
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(i -> "Empresa Test".equals(i.getCompanyName()));
        verify(companyQueryPort).fetchCompany(companyId);
    }

    @Test
    void generateInventoryPdf_callsReportPortAndReturnsByteArray() {
        // given
        byte[] expectedPdf = new byte[]{1, 2, 3};
        when(productQueryPort.fetchProducts()).thenReturn(List.of(item));
        when(companyQueryPort.fetchCompany(companyId)).thenReturn(companyInfo);
        when(reportPort.generateInventoryPdf(any())).thenReturn(expectedPdf);

        // when
        byte[] result = inventoryService.generateInventoryPdf();

        // then
        assertThat(result).isEqualTo(expectedPdf);
        verify(reportPort).generateInventoryPdf(any());
    }

    @Test
    void generateInventoryPdf_whenReportPortThrows_propagatesReportGenerationException() {
        // given
        when(productQueryPort.fetchProducts()).thenReturn(List.of(item));
        when(companyQueryPort.fetchCompany(companyId)).thenReturn(companyInfo);
        when(reportPort.generateInventoryPdf(any()))
                .thenThrow(new ReportGenerationException("Error al generar el reporte PDF."));

        // when / then
        assertThatThrownBy(() -> inventoryService.generateInventoryPdf())
                .isInstanceOf(ReportGenerationException.class)
                .hasMessageContaining("Error al generar el reporte PDF.");
    }

    @Test
    void sendPdfByEmail_callsEmailPortWithCorrectParameters() {
        // given
        byte[] pdfBytes = new byte[]{1, 2, 3};
        String email = "test@example.com";
        when(productQueryPort.fetchProducts()).thenReturn(List.of(item));
        when(companyQueryPort.fetchCompany(companyId)).thenReturn(companyInfo);
        when(reportPort.generateInventoryPdf(any())).thenReturn(pdfBytes);

        // when
        inventoryService.sendPdfByEmail(email);

        // then
        verify(emailPort).sendInventoryEmail(eq(email), eq(pdfBytes));
    }

    @Test
    void sendPdfByEmail_whenEmailPortThrows_propagatesEmailDeliveryException() {
        // given
        byte[] pdfBytes = new byte[]{1, 2, 3};
        String email = "test@example.com";
        when(productQueryPort.fetchProducts()).thenReturn(List.of(item));
        when(companyQueryPort.fetchCompany(companyId)).thenReturn(companyInfo);
        when(reportPort.generateInventoryPdf(any())).thenReturn(pdfBytes);
        doThrow(new EmailDeliveryException("Error al enviar el correo electrónico."))
                .when(emailPort).sendInventoryEmail(eq(email), any());

        // when / then
        assertThatThrownBy(() -> inventoryService.sendPdfByEmail(email))
                .isInstanceOf(EmailDeliveryException.class)
                .hasMessageContaining("Error al enviar el correo electrónico.");
    }
}
