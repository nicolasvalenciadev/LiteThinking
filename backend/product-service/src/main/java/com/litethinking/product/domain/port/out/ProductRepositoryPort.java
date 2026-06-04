package com.litethinking.product.domain.port.out;

import com.litethinking.product.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    List<Product> findAll();
    Optional<Product> findById(UUID id);
    List<Product> findByCompanyId(UUID companyId);
    Optional<Product> findByCode(String code);
    Product save(Product product);
    void softDelete(UUID id);
}
