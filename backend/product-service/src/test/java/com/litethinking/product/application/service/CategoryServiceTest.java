package com.litethinking.product.application.service;

import com.litethinking.product.domain.exception.CategoryNotFoundException;
import com.litethinking.product.domain.model.Category;
import com.litethinking.product.domain.port.out.CategoryRepositoryPort;
import com.litethinking.product.infrastructure.mapper.CategoryMapper;
import com.litethinking.product.infrastructure.web.dto.CategoryRequestDTO;
import com.litethinking.product.infrastructure.web.dto.CategoryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryResponseDTO categoryResponseDTO;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category(categoryId, "Electronics", LocalDateTime.now(), LocalDateTime.now(), false);
        categoryResponseDTO = new CategoryResponseDTO(categoryId, "Electronics", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void findAll_returnsListOfCategories() {
        when(categoryRepositoryPort.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponseDTO(category)).thenReturn(categoryResponseDTO);

        List<CategoryResponseDTO> result = categoryService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    void create_savesAndReturnsCategory() {
        CategoryRequestDTO request = new CategoryRequestDTO("Electronics");
        when(categoryRepositoryPort.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(category)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO result = categoryService.create(request);

        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepositoryPort).save(any(Category.class));
    }

    @Test
    void findById_throwsCategoryNotFoundException_whenNotFound() {
        UUID missingId = UUID.randomUUID();
        when(categoryRepositoryPort.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(missingId))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
