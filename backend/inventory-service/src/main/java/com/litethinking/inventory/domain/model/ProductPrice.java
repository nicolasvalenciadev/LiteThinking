package com.litethinking.inventory.domain.model;

import java.math.BigDecimal;

public class ProductPrice {

    private String currency;
    private BigDecimal price;

    public ProductPrice() {}

    public ProductPrice(String currency, BigDecimal price) {
        this.currency = currency;
        this.price = price;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
