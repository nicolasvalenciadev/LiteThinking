package com.litethinking.company.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponseDTO {

    private UUID id;
    private String name;
    private String nit;
    private String address;
    private String telephone;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
}
