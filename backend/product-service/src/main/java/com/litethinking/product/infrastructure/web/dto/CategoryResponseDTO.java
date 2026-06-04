package com.litethinking.product.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CategoryResponseDTO {

    private UUID id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;

    public CategoryResponseDTO() {}

    public CategoryResponseDTO(UUID id, String name, LocalDateTime createdDate, LocalDateTime lastUpdate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}
