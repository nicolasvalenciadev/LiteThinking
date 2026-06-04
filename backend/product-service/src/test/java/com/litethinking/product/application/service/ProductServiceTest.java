package com.litethinking.product.application.service;

import com.litethinking.product.domain.exception.ProductAlreadyExistsException;
import com.litethinking.product.domain.exception.ProductNotFoundException;
import com.litethinking.product.domain.model.Category;
import com.litethinking.product.domain.model.Product;
import com.litethinking.product.domain.model.ProductPrice;
import com.litethinking.product.domain.port.out.CategoryRepositoryPort;
import com.litethinking.product.domain.port.out.ProductPriceRepositoryPort;
import com.litethinking.product.domain.port.out.ProductRepositoryPort;
import com.litethinking.product.infrastructure.mapper.CategoryMapper;
import com.litethinking.product.infrastructure.mapper.ProductMapper;
import com.litethinking.product.infrastructure.web.dto.ProductPriceDTO;
import com.litethinking.product.infrastructure.web.dto.ProductRequestDTO;
import com.litethinking.product.infrastructure.web.dto.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private ProductPriceRepositoryPort productPriceRepositoryPort;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private UUID productId;
    private UUID companyId;
    private Product product;
    private ProductResponseDTO productResponseDTO;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        product = new Product(productId, "P001", "Test Product", "Description",
                companyId, new ArrayList<>(), new ArrayList<>(),
                LocalDateTime.now(), LocalDateTime.now(), false);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(productId);
        productResponseDTO.setCode("P001");
        productResponseDTO.setName("Test Product");

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setCode("P001");
        productRequestDTO.setName("Test Product");
        productRequestDTO.setDescription("Description");
        productRequestDTO.setCompanyId(companyId);

        ProductPriceDTO priceDTO = new ProductPriceDTO("COP", BigDecimal.valueOf(1000));
        productRequestDTO.setPrices(List.of(priceDTO));
        productRequestDTO.setCategoryIds(new ArrayList<>());
    }

    @Test
    void findAll_returnsListOfProducts() {
        when(productRepositoryPort.findAll()).thenReturn(List.of(product));
        when(productPriceRepositoryPort.findByProductId(productId)).thenReturn(new ArrayList<>());
        when(categoryRepositoryPort.findByProductId(productId)).thenReturn(new ArrayList<>());
        when(productMapper.toResponseDTO(any(Product.class))).thenReturn(productResponseDTO);

        List<ProductResponseDTO> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("P001");
    }

    @Test
    void findById_returnsProduct_whenExists() {
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(product));
        when(productPriceRepositoryPort.findByProductId(productId)).thenReturn(new ArrayList<>());
        when(categoryRepositoryPort.findByProductId(productId)).thenReturn(new ArrayList<>());
        when(productMapper.toResponseDTO(any(Product.class))).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.findById(productId);

        assertThat(result.getCode()).isEqualTo("P001");
    }

    @Test
    void findById_throwsProductNotFoundException_whenNotFound() {
        UUID missingId = UUID.randomUUID();
        when(productRepositoryPort.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(missingId))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void create_savesProductWithPricesAndCategories() {
        when(productRepositoryPort.findByCode("P001")).thenReturn(Optional.empty());
        when(productRepositoryPort.save(any(Product.class))).thenReturn(product);
        when(productPriceRepositoryPort.save(any(ProductPrice.class))).thenReturn(new ProductPrice());
        when(productPriceRepositoryPort.findByProductId(productId)).thenReturn(new ArrayList<>());
        when(categoryRepositoryPort.findByProductId(productId)).thenReturn(new ArrayList<>());
        when(productMapper.toResponseDTO(any(Product.class))).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.create(productRequestDTO);

        assertThat(result).isNotNull();
        verify(productRepositoryPort).save(any(Product.class));
        verify(productPriceRepositoryPort).save(any(ProductPrice.class));
    }

    @Test
    void create_throwsProductAlreadyExistsException_whenCodeExists() {
        when(productRepositoryPort.findByCode("P001")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.create(productRequestDTO))
                .isInstanceOf(ProductAlreadyExistsException.class);

        verify(productRepositoryPort, never()).save(any());
    }

    @Test
    void delete_performsSoftDeleteOnProductPricesAndCategoryProduct() {
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(product));

        productService.delete(productId);

        verify(productRepositoryPort).softDelete(productId);
        verify(productPriceRepositoryPort).softDeleteByProductId(productId);
        verify(categoryRepositoryPort).softDeleteCategoryProductByProductId(productId);
    }
}
