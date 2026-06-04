package com.litethinking.inventory.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryItem {

    private UUID productId;
    private String productCode;
    private String productName;
    private String productDescription;
    private UUID companyId;
    private String companyName;
    private String companyNit;
    private List<ProductPrice> prices = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    public InventoryItem() {}

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyNit() { return companyNit; }
    public void setCompanyNit(String companyNit) { this.companyNit = companyNit; }

    public List<ProductPrice> getPrices() { return prices; }
    public void setPrices(List<ProductPrice> prices) { this.prices = prices; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public String getPriceByCurrency(String currency) {
        return prices.stream()
                .filter(p -> currency.equalsIgnoreCase(p.getCurrency()))
                .map(p -> p.getPrice() != null ? p.getPrice().toPlainString() : "")
                .findFirst()
                .orElse("");
    }

    public String getPriceCOP() { return getPriceByCurrency("COP"); }
    public String getPriceUSD() { return getPriceByCurrency("USD"); }
    public String getPriceEUR() { return getPriceByCurrency("EUR"); }

    public String getCategoriesAsString() {
        return String.join(", ", categories);
    }
}
