package com.litethinking.product.application.service;

import com.litethinking.product.domain.exception.CategoryNotFoundException;
import com.litethinking.product.domain.model.Category;
import com.litethinking.product.domain.port.in.CategoryUseCase;
import com.litethinking.product.domain.port.out.CategoryRepositoryPort;
import com.litethinking.product.infrastructure.mapper.CategoryMapper;
import com.litethinking.product.infrastructure.web.dto.CategoryRequestDTO;
import com.litethinking.product.infrastructure.web.dto.CategoryResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService implements CategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponseDTO> findAll() {
        return categoryRepositoryPort.findAll().stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    @Override
    public CategoryResponseDTO findById(UUID id) {
        Category category = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        Category category = new Category();
        category.setName(request.getName());
        Category saved = categoryRepositoryPort.save(category);
        return categoryMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public CategoryResponseDTO update(UUID id, CategoryRequestDTO request) {
        Category category = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.setName(request.getName());
        Category updated = categoryRepositoryPort.save(category);
        return categoryMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        categoryRepositoryPort.softDelete(id);
    }
}
