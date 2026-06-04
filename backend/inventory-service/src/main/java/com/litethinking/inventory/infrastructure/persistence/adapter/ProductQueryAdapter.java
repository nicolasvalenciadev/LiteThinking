package com.litethinking.inventory.infrastructure.persistence.adapter;

import com.litethinking.inventory.domain.model.InventoryItem;
import com.litethinking.inventory.domain.model.ProductPrice;
import com.litethinking.inventory.domain.port.out.ProductQueryPort;
import com.litethinking.inventory.infrastructure.persistence.entity.CategoryProductEntity;
import com.litethinking.inventory.infrastructure.persistence.entity.ProductEntity;
import com.litethinking.inventory.infrastructure.persistence.entity.ProductPriceEntity;
import com.litethinking.inventory.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductJpaRepository productJpaRepository;

    public ProductQueryAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<InventoryItem> fetchProducts() {
        // Query 1: products with prices
        List<ProductEntity> withPrices = productJpaRepository.findAllWithPrices();

        // Query 2: products with categories
        List<ProductEntity> withCategories = productJpaRepository.findAllWithCategories();

        // Map categories by product id
        Map<UUID, List<String>> categoriesByProduct = withCategories.stream()
                .collect(Collectors.toMap(
                        ProductEntity::getId,
                        p -> p.getCategoryProducts().stream()
                                .map(CategoryProductEntity::getCategory)
                                .map(cat -> cat.getName())
                                .collect(Collectors.toList()),
                        (a, b) -> a
                ));

        return withPrices.stream()
                .map(entity -> toInventoryItem(entity, categoriesByProduct.getOrDefault(entity.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private InventoryItem toInventoryItem(ProductEntity entity, List<String> categories) {
        InventoryItem item = new InventoryItem();
        item.setProductId(entity.getId());
        item.setProductCode(entity.getCode());
        item.setProductName(entity.getName());
        item.setProductDescription(entity.getDescription());
        item.setCompanyId(entity.getCompanyId());

        List<ProductPrice> prices = entity.getPrices().stream()
                .map(this::toProductPrice)
                .collect(Collectors.toList());
        item.setPrices(prices);
        item.setCategories(categories);

        return item;
    }

    private ProductPrice toProductPrice(ProductPriceEntity entity) {
        return new ProductPrice(entity.getCurrency(), entity.getPrice());
    }
}
