package com.litethinking.product.application.service;

import com.litethinking.product.domain.exception.CategoryNotFoundException;
import com.litethinking.product.domain.exception.ProductAlreadyExistsException;
import com.litethinking.product.domain.exception.ProductNotFoundException;
import com.litethinking.product.domain.exception.ProductPersistenceException;
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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepositoryPort.findAll().stream()
                .map(this::enrichAndMap)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(UUID id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return enrichAndMap(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByCompany(UUID companyId) {
        return productRepositoryPort.findByCompanyId(companyId).stream()
                .map(this::enrichAndMap)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponseDTO create(ProductRequestDTO request) {
        // Step 1: Verify no other product already uses this code
        productRepositoryPort.findByCode(request.getCode()).ifPresent(p -> {
            throw new ProductAlreadyExistsException(request.getCode());
        });

        // Step 2: Persist the new product record
        Product saved;
        try {
            Product product = new Product();
            product.setCode(request.getCode());
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setCompanyId(request.getCompanyId());
            saved = productRepositoryPort.save(product);
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al persistir el producto", ex);
        }

        // Step 3: Persist each price row — on failure the transaction rolls back Step 2
        try {
            savePrices(saved.getId(), request.getPrices());
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al persistir los precios del producto", ex);
        }

        // Step 4: Persist category links — on failure the transaction rolls back Steps 2 and 3
        try {
            saveCategories(saved.getId(), request.getCategoryIds());
        } catch (CategoryNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al persistir las categorías del producto", ex);
        }

        // Step 5: Reload prices and categories, then map to response DTO
        return enrichAndMap(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponseDTO update(UUID id, ProductRequestDTO request) {
        // Step 1: Verify the product to update exists
        Product existing = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Step 2: Verify the new code is not already taken by a different product
        productRepositoryPort.findByCode(request.getCode())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new ProductAlreadyExistsException(request.getCode());
                });

        // Step 3: Persist updated product fields
        Product updated;
        try {
            existing.setCode(request.getCode());
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            existing.setCompanyId(request.getCompanyId());
            updated = productRepositoryPort.save(existing);
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al actualizar el producto", ex);
        }

        // Step 4: Replace prices — soft-delete old rows, then insert new ones atomically
        try {
            productPriceRepositoryPort.softDeleteByProductId(id);
            savePrices(id, request.getPrices());
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al actualizar los precios del producto", ex);
        }

        // Step 5: Replace category links — soft-delete old rows, then insert new ones atomically
        try {
            categoryRepositoryPort.softDeleteCategoryProductByProductId(id);
            saveCategories(id, request.getCategoryIds());
        } catch (CategoryNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al actualizar las categorías del producto", ex);
        }

        // Step 6: Reload enriched product and map to response DTO
        return enrichAndMap(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID id) {
        // Step 1: Verify the product exists before attempting deletion
        productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Step 2: Soft-delete product, prices, and category links in a single atomic operation
        try {
            productRepositoryPort.softDelete(id);
            productPriceRepositoryPort.softDeleteByProductId(id);
            categoryRepositoryPort.softDeleteCategoryProductByProductId(id);
        } catch (DataAccessException ex) {
            throw new ProductPersistenceException("Error al eliminar el producto", ex);
        }
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
