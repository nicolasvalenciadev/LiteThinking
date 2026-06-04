package com.litethinking.product.infrastructure.web.controller;

import com.litethinking.product.domain.exception.ForbiddenException;
import com.litethinking.product.domain.port.in.CategoryUseCase;
import com.litethinking.product.infrastructure.web.dto.CategoryRequestDTO;
import com.litethinking.product.infrastructure.web.dto.CategoryResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;

    public CategoryController(CategoryUseCase categoryUseCase) {
        this.categoryUseCase = categoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll(
            @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(categoryUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(categoryUseCase.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(
            @Valid @RequestBody CategoryRequestDTO request,
            @RequestHeader("X-User-Role") String userRole) {
        requireAdmin(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryUseCase.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequestDTO request,
            @RequestHeader("X-User-Role") String userRole) {
        requireAdmin(userRole);
        return ResponseEntity.ok(categoryUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        requireAdmin(userRole);
        categoryUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin(String userRole) {
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            throw new ForbiddenException();
        }
    }
}
