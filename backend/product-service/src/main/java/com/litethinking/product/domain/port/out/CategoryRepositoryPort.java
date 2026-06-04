package com.litethinking.product.domain.port.out;

import com.litethinking.product.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    List<Category> findAll();
    Optional<Category> findById(UUID id);
    List<Category> findByProductId(UUID productId);
    Category save(Category category);
    void softDelete(UUID id);
    void saveCategoryProduct(UUID categoryId, UUID productId);
    void softDeleteCategoryProductByProductId(UUID productId);
}
