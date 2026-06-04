package com.litethinking.product.infrastructure.persistence.adapter;

import com.litethinking.product.domain.model.Category;
import com.litethinking.product.domain.port.out.CategoryRepositoryPort;
import com.litethinking.product.infrastructure.mapper.CategoryMapper;
import com.litethinking.product.infrastructure.persistence.entity.CategoryEntity;
import com.litethinking.product.infrastructure.persistence.entity.CategoryProductEntity;
import com.litethinking.product.infrastructure.persistence.repository.CategoryJpaRepository;
import com.litethinking.product.infrastructure.persistence.repository.CategoryProductJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryProductJpaRepository categoryProductJpaRepository;
    private final CategoryMapper mapper;

    public CategoryRepositoryAdapter(CategoryJpaRepository categoryJpaRepository,
                                     CategoryProductJpaRepository categoryProductJpaRepository,
                                     CategoryMapper mapper) {
        this.categoryJpaRepository = categoryJpaRepository;
        this.categoryProductJpaRepository = categoryProductJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Category> findByProductId(UUID productId) {
        List<UUID> categoryIds = categoryProductJpaRepository.findCategoryIdsByProductId(productId);
        return categoryIds.stream()
                .map(categoryJpaRepository::findById)
                .filter(Optional::isPresent)
                .map(opt -> mapper.toDomain(opt.get()))
                .toList();
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        CategoryEntity saved = categoryJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        categoryJpaRepository.softDeleteById(id);
    }

    @Override
    public void saveCategoryProduct(UUID categoryId, UUID productId) {
        CategoryProductEntity entity = new CategoryProductEntity();
        entity.setCategoryId(categoryId);
        entity.setProductId(productId);
        categoryProductJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void softDeleteCategoryProductByProductId(UUID productId) {
        categoryProductJpaRepository.softDeleteByProductId(productId);
    }
}
