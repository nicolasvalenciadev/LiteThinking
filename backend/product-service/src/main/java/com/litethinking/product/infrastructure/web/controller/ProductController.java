package com.litethinking.product.infrastructure.web.controller;

import com.litethinking.product.domain.exception.ForbiddenException;
import com.litethinking.product.domain.port.in.ProductUseCase;
import com.litethinking.product.infrastructure.web.dto.ProductRequestDTO;
import com.litethinking.product.infrastructure.web.dto.ProductResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final String ROLE_ADMIN = "ADMIN";

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll(
            @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(productUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(productUseCase.findById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ProductResponseDTO>> findByCompany(
            @PathVariable UUID companyId,
            @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(productUseCase.findByCompany(companyId));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO request,
            @RequestHeader("X-User-Role") String userRole) {
        requireAdmin(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(productUseCase.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequestDTO request,
            @RequestHeader("X-User-Role") String userRole) {
        requireAdmin(userRole);
        return ResponseEntity.ok(productUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        requireAdmin(userRole);
        productUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin(String userRole) {
        if (!ROLE_ADMIN.equalsIgnoreCase(userRole)) {
            throw new ForbiddenException();
        }
    }
}
