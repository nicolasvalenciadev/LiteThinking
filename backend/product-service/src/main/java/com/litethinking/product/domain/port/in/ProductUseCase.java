package com.litethinking.product.domain.port.in;

import com.litethinking.product.infrastructure.web.dto.ProductRequestDTO;
import com.litethinking.product.infrastructure.web.dto.ProductResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ProductUseCase {
    List<ProductResponseDTO> findAll();
    ProductResponseDTO findById(UUID id);
    List<ProductResponseDTO> findByCompany(UUID companyId);
    ProductResponseDTO create(ProductRequestDTO request);
    ProductResponseDTO update(UUID id, ProductRequestDTO request);
    void delete(UUID id);
}
