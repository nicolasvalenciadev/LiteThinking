package com.litethinking.product.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class ProductRequestDTO {

    @NotBlank(message = "El código del producto es obligatorio")
    private String code;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El identificador de la empresa es obligatorio")
    private UUID companyId;

    private List<UUID> categoryIds;

    @NotEmpty(message = "Debe ingresar al menos un precio")
    @Valid
    private List<ProductPriceDTO> prices;

    public ProductRequestDTO() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public List<UUID> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<UUID> categoryIds) { this.categoryIds = categoryIds; }

    public List<ProductPriceDTO> getPrices() { return prices; }
    public void setPrices(List<ProductPriceDTO> prices) { this.prices = prices; }
}
