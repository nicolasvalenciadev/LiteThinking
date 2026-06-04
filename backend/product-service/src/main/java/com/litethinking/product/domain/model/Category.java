package com.litethinking.product.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Category {

    private UUID id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private boolean deleted;

    public Category() {}

    public Category(UUID id, String name, LocalDateTime createdDate,
                    LocalDateTime lastUpdate, boolean deleted) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
        this.deleted = deleted;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
