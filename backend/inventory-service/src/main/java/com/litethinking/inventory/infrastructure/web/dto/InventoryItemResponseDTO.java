package com.litethinking.inventory.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class InventoryItemResponseDTO {

    private UUID productId;
    private String productCode;
    private String productName;
    private String productDescription;
    private UUID companyId;
    private String companyName;
    private String companyNit;
    private List<PriceDTO> prices;
    private List<String> categories;

    public InventoryItemResponseDTO() {}

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

    public List<PriceDTO> getPrices() { return prices; }
    public void setPrices(List<PriceDTO> prices) { this.prices = prices; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public static class PriceDTO {
        private String currency;
        private BigDecimal price;

        public PriceDTO() {}

        public PriceDTO(String currency, BigDecimal price) {
            this.currency = currency;
            this.price = price;
        }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}
