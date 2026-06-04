package com.litethinking.product.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Product {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private UUID companyId;
    private List<ProductPrice> prices;
    private List<Category> categories;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private boolean deleted;

    public Product() {
        this.prices = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public Product(UUID id, String code, String name, String description, UUID companyId,
                   List<ProductPrice> prices, List<Category> categories,
                   LocalDateTime createdDate, LocalDateTime lastUpdate, boolean deleted) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.prices = prices != null ? prices : new ArrayList<>();
        this.categories = categories != null ? categories : new ArrayList<>();
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
        this.deleted = deleted;
    }

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

    public List<ProductPrice> getPrices() { return prices; }
    public void setPrices(List<ProductPrice> prices) { this.prices = prices; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
