package com.litethinking.product.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductResponseDTO {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private UUID companyId;
    private List<ProductPriceDTO> prices;
    private List<CategoryResponseDTO> categories;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;

    public ProductResponseDTO() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public List<ProductPriceDTO> getPrices() { return prices; }
    public void setPrices(List<ProductPriceDTO> prices) { this.prices = prices; }

    public List<CategoryResponseDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryResponseDTO> categories) { this.categories = categories; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}
