package com.litethinking.product.domain.port.in;

import com.litethinking.product.infrastructure.web.dto.CategoryRequestDTO;
import com.litethinking.product.infrastructure.web.dto.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryUseCase {
    List<CategoryResponseDTO> findAll();
    CategoryResponseDTO findById(UUID id);
    CategoryResponseDTO create(CategoryRequestDTO request);
    CategoryResponseDTO update(UUID id, CategoryRequestDTO request);
    void delete(UUID id);
}
