package com.litethinking.company.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Company {

    private UUID id;
    private String name;
    private String nit;
    private String address;
    private String telephone;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private boolean deleted;

    public Company() {}

    public Company(UUID id, String name, String nit, String address, String telephone,
                   LocalDateTime createdDate, LocalDateTime lastUpdate, boolean deleted) {
        this.id = id;
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.telephone = telephone;
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
        this.deleted = deleted;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
