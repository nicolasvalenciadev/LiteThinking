package com.litethinking.product.application.service;

import com.litethinking.product.domain.exception.CategoryNotFoundException;
import com.litethinking.product.domain.exception.ProductAlreadyExistsException;
import com.litethinking.product.domain.exception.ProductNotFoundException;
import com.litethinking.product.domain.model.Category;
import com.litethinking.product.domain.model.Product;
import com.litethinking.product.domain.model.ProductPrice;
import com.litethinking.product.domain.port.in.ProductUseCase;
import com.litethinking.product.domain.port.out.CategoryRepositoryPort;
import com.litethinking.product.domain.port.out.ProductPriceRepositoryPort;
import com.litethinking.product.domain.port.out.ProductRepositoryPort;
import com.litethinking.product.infrastructure.mapper.ProductMapper;
import com.litethinking.product.infrastructure.web.dto.ProductPriceDTO;
import com.litethinking.product.infrastructure.web.dto.ProductRequestDTO;
import com.litethinking.product.infrastructure.web.dto.ProductResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductPriceRepositoryPort productPriceRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final ProductMapper productMapper;

    public ProductService(ProductRepositoryPort productRepositoryPort,
                          ProductPriceRepositoryPort productPriceRepositoryPort,
                          CategoryRepositoryPort categoryRepositoryPort,
                          ProductMapper productMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.productPriceRepositoryPort = productPriceRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return productRepositoryPort.findAll().stream()
                .map(this::enrichAndMap)
                .toList();
    }

    @Override
    public ProductResponseDTO findById(UUID id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return enrichAndMap(product);
    }

    @Override
    public List<ProductResponseDTO> findByCompany(UUID companyId) {
        return productRepositoryPort.findByCompanyId(companyId).stream()
                .map(this::enrichAndMap)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        productRepositoryPort.findByCode(request.getCode()).ifPresent(p -> {
            throw new ProductAlreadyExistsException(request.getCode());
        });

        Product product = new Product();
        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCompanyId(request.getCompanyId());
        Product saved = productRepositoryPort.save(product);

        savePrices(saved.getId(), request.getPrices());
        saveCategories(saved.getId(), request.getCategoryIds());

        return enrichAndMap(saved);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(UUID id, ProductRequestDTO request) {
        Product existing = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepositoryPort.findByCode(request.getCode())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new ProductAlreadyExistsException(request.getCode());
                });

        existing.setCode(request.getCode());
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCompanyId(request.getCompanyId());
        Product updated = productRepositoryPort.save(existing);

        productPriceRepositoryPort.softDeleteByProductId(id);
        savePrices(id, request.getPrices());

        categoryRepositoryPort.softDeleteCategoryProductByProductId(id);
        saveCategories(id, request.getCategoryIds());

        return enrichAndMap(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepositoryPort.softDelete(id);
        productPriceRepositoryPort.softDeleteByProductId(id);
        categoryRepositoryPort.softDeleteCategoryProductByProductId(id);
    }

    private void savePrices(UUID productId, List<ProductPriceDTO> priceDTOs) {
        if (priceDTOs == null) return;
        for (ProductPriceDTO dto : priceDTOs) {
            ProductPrice price = new ProductPrice();
            price.setProductId(productId);
            price.setCurrency(dto.getCurrency());
            price.setPrice(dto.getPrice());
            productPriceRepositoryPort.save(price);
        }
    }

    private void saveCategories(UUID productId, List<UUID> categoryIds) {
        if (categoryIds == null) return;
        for (UUID categoryId : categoryIds) {
            categoryRepositoryPort.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            categoryRepositoryPort.saveCategoryProduct(categoryId, productId);
        }
    }

    private ProductResponseDTO enrichAndMap(Product product) {
        List<ProductPrice> prices = productPriceRepositoryPort.findByProductId(product.getId());
        product.setPrices(prices);

        List<Category> categories = categoryRepositoryPort.findByProductId(product.getId());
        product.setCategories(categories);

        return productMapper.toResponseDTO(product);
    }
}
