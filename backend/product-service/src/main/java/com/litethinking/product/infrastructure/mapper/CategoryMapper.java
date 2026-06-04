package com.litethinking.product.infrastructure.mapper;

import com.litethinking.product.domain.model.Category;
import com.litethinking.product.infrastructure.persistence.entity.CategoryEntity;
import com.litethinking.product.infrastructure.web.dto.CategoryResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryEntity entity) {
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.getCreatedDate(),
                entity.getLastUpdate(),
                entity.isDeleted()
        );
    }

    public CategoryEntity toEntity(Category domain) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDeleted(domain.isDeleted());
        return entity;
    }

    public CategoryResponseDTO toResponseDTO(Category domain) {
        return new CategoryResponseDTO(
                domain.getId(),
                domain.getName(),
                domain.getCreatedDate(),
                domain.getLastUpdate()
        );
    }
}
