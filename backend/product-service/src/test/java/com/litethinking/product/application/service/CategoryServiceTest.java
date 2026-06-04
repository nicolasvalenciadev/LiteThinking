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

    private UUID categoryId;
    private Category category;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category(categoryId, "Electronics", LocalDateTime.now(), LocalDateTime.now(), false);
        categoryResponseDTO = new CategoryResponseDTO(categoryId, "Electronics", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void findAll_returnsListOfCategories() {
        // given
        when(categoryRepositoryPort.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponseDTO(category)).thenReturn(categoryResponseDTO);

        // when
        List<CategoryResponseDTO> result = categoryService.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    void findById_returnsCategory_whenFound() {
        // given
        when(categoryRepositoryPort.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDTO(category)).thenReturn(categoryResponseDTO);

        // when
        CategoryResponseDTO result = categoryService.findById(categoryId);

        // then
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void findById_throwsCategoryNotFoundException_whenNotFound() {
        // given
        UUID missingId = UUID.randomUUID();
        when(categoryRepositoryPort.findById(missingId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> categoryService.findById(missingId))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void create_savesAndReturnsCategory() {
        // given
        CategoryRequestDTO request = new CategoryRequestDTO("Electronics");
        when(categoryRepositoryPort.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(category)).thenReturn(categoryResponseDTO);

        // when
        CategoryResponseDTO result = categoryService.create(request);

        // then
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepositoryPort).save(any(Category.class));
    }

    @Test
    void update_returnsUpdatedCategory_whenFound() {
        // given
        CategoryRequestDTO request = new CategoryRequestDTO("Updated Electronics");
        Category updated = new Category(categoryId, "Updated Electronics", LocalDateTime.now(), LocalDateTime.now(), false);
        CategoryResponseDTO updatedResponse = new CategoryResponseDTO(categoryId, "Updated Electronics", LocalDateTime.now(), LocalDateTime.now());
        when(categoryRepositoryPort.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepositoryPort.save(any(Category.class))).thenReturn(updated);
        when(categoryMapper.toResponseDTO(updated)).thenReturn(updatedResponse);

        // when
        CategoryResponseDTO result = categoryService.update(categoryId, request);

        // then
        assertThat(result.getName()).isEqualTo("Updated Electronics");
        verify(categoryRepositoryPort).save(any(Category.class));
    }

    @Test
    void delete_softDeletesCategory_whenFound() {
        // given
        when(categoryRepositoryPort.findById(categoryId)).thenReturn(Optional.of(category));

        // when
        categoryService.delete(categoryId);

        // then
        verify(categoryRepositoryPort).softDelete(categoryId);
    }
}
