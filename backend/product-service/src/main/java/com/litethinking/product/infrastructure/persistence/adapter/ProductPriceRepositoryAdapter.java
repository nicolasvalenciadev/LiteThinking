package com.litethinking.product.infrastructure.persistence.adapter;

import com.litethinking.product.domain.model.ProductPrice;
import com.litethinking.product.domain.port.out.ProductPriceRepositoryPort;
import com.litethinking.product.infrastructure.mapper.ProductMapper;
import com.litethinking.product.infrastructure.persistence.entity.ProductPriceEntity;
import com.litethinking.product.infrastructure.persistence.repository.ProductPriceJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class ProductPriceRepositoryAdapter implements ProductPriceRepositoryPort {

    private final ProductPriceJpaRepository jpaRepository;
    private final ProductMapper mapper;

    public ProductPriceRepositoryAdapter(ProductPriceJpaRepository jpaRepository, ProductMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductPrice> findByProductId(UUID productId) {
        return jpaRepository.findByProductId(productId).stream()
                .map(mapper::priceToDomain)
                .toList();
    }

    @Override
    public ProductPrice save(ProductPrice productPrice) {
        ProductPriceEntity entity = mapper.priceToEntity(productPrice);
        ProductPriceEntity saved = jpaRepository.save(entity);
        return mapper.priceToDomain(saved);
    }

    @Override
    @Transactional
    public void softDeleteByProductId(UUID productId) {
        jpaRepository.softDeleteByProductId(productId);
    }
}
