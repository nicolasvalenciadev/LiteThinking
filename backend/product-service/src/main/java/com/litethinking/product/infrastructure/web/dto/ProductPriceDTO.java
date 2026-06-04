package com.litethinking.product.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class ProductPriceDTO {

    @NotBlank(message = "La moneda es obligatoria")
    @Pattern(regexp = "COP|USD|EUR", message = "La moneda debe ser COP, USD o EUR")
    private String currency;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal price;

    public ProductPriceDTO() {}

    public ProductPriceDTO(String currency, BigDecimal price) {
        this.currency = currency;
        this.price = price;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
