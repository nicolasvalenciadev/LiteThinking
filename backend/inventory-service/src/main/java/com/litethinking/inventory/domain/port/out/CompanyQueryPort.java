package com.litethinking.inventory.domain.port.out;

import com.litethinking.inventory.domain.model.CompanyInfo;

import java.util.UUID;

public interface CompanyQueryPort {

    CompanyInfo fetchCompany(UUID id);
}
