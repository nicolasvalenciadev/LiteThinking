package com.litethinking.product.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequestDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    public CategoryRequestDTO() {}

    public CategoryRequestDTO(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
