package com.litethinking.company.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
    private String name;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 255, message = "El NIT no puede superar los 255 caracteres")
    private String nit;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String address;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telephone;
}
