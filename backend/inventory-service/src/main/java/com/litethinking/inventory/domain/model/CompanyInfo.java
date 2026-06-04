package com.litethinking.inventory.domain.model;

import java.util.UUID;

public class CompanyInfo {

    private UUID id;
    private String name;
    private String nit;
    private String address;
    private String telephone;

    public CompanyInfo() {}

    public CompanyInfo(UUID id, String name, String nit, String address, String telephone) {
        this.id = id;
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.telephone = telephone;
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
}
