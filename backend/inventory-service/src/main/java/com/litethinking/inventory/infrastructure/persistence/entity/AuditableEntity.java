package com.litethinking.inventory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

@MappedSuperclass
public abstract class AuditableEntity {

    @Column(name = "created_date", updatable = false)
    private Instant createdDate;

    @Column(name = "last_update")
    private Instant lastUpdate;

    @PrePersist
    protected void onCreate() {
        createdDate = Instant.now();
        lastUpdate = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = Instant.now();
    }

    public Instant getCreatedDate() { return createdDate; }
    public Instant getLastUpdate() { return lastUpdate; }
}
