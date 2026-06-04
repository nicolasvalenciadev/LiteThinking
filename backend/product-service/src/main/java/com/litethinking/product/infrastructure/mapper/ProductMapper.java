package com.litethinking.product.infrastructure.mapper;

import com.litethinking.product.domain.model.Product;
import com.litethinking.product.domain.model.ProductPrice;
import com.litethinking.product.infrastructure.persistence.entity.ProductEntity;
import com.litethinking.product.infrastructure.persistence.entity.ProductPriceEntity;
import com.litethinking.product.infrastructure.web.dto.ProductPriceDTO;
import com.litethinking.product.infrastructure.web.dto.ProductResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Product toDomain(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setCode(entity.getCode());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setCompanyId(entity.getCompanyId());
        product.setCreatedDate(entity.getCreatedDate());
        product.setLastUpdate(entity.getLastUpdate());
        product.setDeleted(entity.isDeleted());
        return product;
    }

    public ProductEntity toEntity(Product domain) {
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCompanyId(domain.getCompanyId());
        entity.setDeleted(domain.isDeleted());
        return entity;
    }

    public ProductPrice priceToDomain(ProductPriceEntity entity) {
        return new ProductPrice(
                entity.getId(),
                entity.getProductId(),
                entity.getCurrency(),
                entity.getPrice(),
                entity.getCreatedDate(),
                entity.getLastUpdate(),
                entity.isDeleted()
        );
    }

    public ProductPriceEntity priceToEntity(ProductPrice domain) {
        ProductPriceEntity entity = new ProductPriceEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setCurrency(domain.getCurrency());
        entity.setPrice(domain.getPrice());
        entity.setDeleted(domain.isDeleted());
        return entity;
    }

    public ProductResponseDTO toResponseDTO(Product domain) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(domain.getId());
        dto.setCode(domain.getCode());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        dto.setCompanyId(domain.getCompanyId());
        dto.setCreatedDate(domain.getCreatedDate());
        dto.setLastUpdate(domain.getLastUpdate());

        List<ProductPriceDTO> priceDTOs = domain.getPrices().stream()
                .map(p -> new ProductPriceDTO(p.getCurrency(), p.getPrice()))
                .toList();
        dto.setPrices(priceDTOs);

        List<com.litethinking.product.infrastructure.web.dto.CategoryResponseDTO> categoryDTOs =
                domain.getCategories().stream()
                        .map(categoryMapper::toResponseDTO)
                        .toList();
        dto.setCategories(categoryDTOs);

        return dto;
    }
}
