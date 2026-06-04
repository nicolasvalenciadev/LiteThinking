package com.litethinking.inventory.infrastructure.web.controller;

import com.litethinking.inventory.domain.exception.ForbiddenException;
import com.litethinking.inventory.domain.model.InventoryItem;
import com.litethinking.inventory.domain.port.in.InventoryUseCase;
import com.litethinking.inventory.infrastructure.web.dto.InventoryItemResponseDTO;
import com.litethinking.inventory.infrastructure.web.dto.SendEmailRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final String ROLE_HEADER = "X-User-Role";
    private static final String ADMIN_ROLE = "ADMIN";

    private final InventoryUseCase inventoryUseCase;

    public InventoryController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponseDTO>> getInventory(
            @RequestHeader(value = ROLE_HEADER, required = false) String role) {
        requireAdmin(role);
        List<InventoryItemResponseDTO> response = inventoryUseCase.getInventory()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateAndDownloadPdf(
            @RequestHeader(value = ROLE_HEADER, required = false) String role) {
        requireAdmin(role);
        byte[] pdf = inventoryUseCase.generatePdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("inventario.pdf").build());
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(
            @RequestHeader(value = ROLE_HEADER, required = false) String role,
            @Valid @RequestBody SendEmailRequestDTO request) {
        requireAdmin(role);
        inventoryUseCase.sendPdfByEmail(request.getEmail());
        return ResponseEntity.ok(
                "El reporte de inventario fue enviado exitosamente al correo: " + request.getEmail());
    }

    private void requireAdmin(String role) {
        if (!ADMIN_ROLE.equals(role)) {
            throw new ForbiddenException(
                    "Acceso denegado. Solo los administradores pueden acceder a este recurso.");
        }
    }

    private InventoryItemResponseDTO toResponseDTO(InventoryItem item) {
        InventoryItemResponseDTO dto = new InventoryItemResponseDTO();
        dto.setProductId(item.getProductId());
        dto.setProductCode(item.getProductCode());
        dto.setProductName(item.getProductName());
        dto.setProductDescription(item.getProductDescription());
        dto.setCompanyId(item.getCompanyId());
        dto.setCompanyName(item.getCompanyName());
        dto.setCompanyNit(item.getCompanyNit());
        dto.setCategories(item.getCategories());
        dto.setPrices(item.getPrices().stream()
                .map(p -> new InventoryItemResponseDTO.PriceDTO(p.getCurrency(), p.getPrice()))
                .collect(Collectors.toList()));
        return dto;
    }
}
