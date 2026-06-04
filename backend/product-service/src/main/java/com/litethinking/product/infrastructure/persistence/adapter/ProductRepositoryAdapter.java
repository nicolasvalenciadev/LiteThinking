package com.litethinking.product.infrastructure.persistence.adapter;

import com.litethinking.product.domain.model.Product;
import com.litethinking.product.domain.port.out.ProductRepositoryPort;
import com.litethinking.product.infrastructure.mapper.ProductMapper;
import com.litethinking.product.infrastructure.persistence.entity.ProductEntity;
import com.litethinking.product.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository, ProductMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return jpaRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        jpaRepository.softDeleteById(id);
    }
}
