package com.litethinking.product.infrastructure.persistence.adapter;

import com.litethinking.product.domain.exception.ProductPersistenceException;
import com.litethinking.product.domain.model.Product;
import com.litethinking.product.domain.port.out.ProductRepositoryPort;
import com.litethinking.product.infrastructure.mapper.ProductMapper;
import com.litethinking.product.infrastructure.persistence.entity.ProductEntity;
import com.litethinking.product.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.dao.DataAccessException;
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
        try {
            return jpaRepository.findAll().stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al consultar los productos", ex);
        }
    }

    @Override
    public Optional<Product> findById(UUID id) {
        try {
            return jpaRepository.findById(id).map(mapper::toDomain);
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al consultar el producto con id " + id, ex);
        }
    }

    @Override
    public List<Product> findByCompanyId(UUID companyId) {
        try {
            return jpaRepository.findByCompanyId(companyId).stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al consultar productos por empresa", ex);
        }
    }

    @Override
    public Optional<Product> findByCode(String code) {
        try {
            return jpaRepository.findByCode(code).map(mapper::toDomain);
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al consultar el producto con código " + code, ex);
        }
    }

    @Override
    public Product save(Product product) {
        try {
            ProductEntity entity = mapper.toEntity(product);
            ProductEntity saved = jpaRepository.save(entity);
            return mapper.toDomain(saved);
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al guardar el producto", ex);
        }
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        try {
            jpaRepository.softDeleteById(id);
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al eliminar el producto con id " + id, ex);
        }
    }
}
