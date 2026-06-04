package com.litethinking.inventory.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendEmailRequestDTO {

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "El correo electrónico no tiene un formato válido.")
    private String email;

    public SendEmailRequestDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
