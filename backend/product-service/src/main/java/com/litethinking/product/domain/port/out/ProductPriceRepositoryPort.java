package com.litethinking.product.domain.port.out;

import com.litethinking.product.domain.model.ProductPrice;

import java.util.List;
import java.util.UUID;

public interface ProductPriceRepositoryPort {
    List<ProductPrice> findByProductId(UUID productId);
    ProductPrice save(ProductPrice productPrice);
    void softDeleteByProductId(UUID productId);
}
