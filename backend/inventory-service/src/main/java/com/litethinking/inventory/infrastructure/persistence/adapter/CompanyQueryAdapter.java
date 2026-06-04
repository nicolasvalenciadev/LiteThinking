package com.litethinking.inventory.infrastructure.persistence.adapter;

import com.litethinking.inventory.domain.model.CompanyInfo;
import com.litethinking.inventory.domain.port.out.CompanyQueryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;

@Component
public class CompanyQueryAdapter implements CompanyQueryPort {

    private static final Logger log = LoggerFactory.getLogger(CompanyQueryAdapter.class);

    private final WebClient companyWebClient;

    public CompanyQueryAdapter(WebClient companyWebClient) {
        this.companyWebClient = companyWebClient;
    }

    @Override
    public CompanyInfo fetchCompany(UUID id) {
        try {
            CompanyResponse response = companyWebClient.get()
                    .uri("/api/companies/{id}", id)
                    .header("X-User-Role", "ADMIN")
                    .retrieve()
                    .bodyToMono(CompanyResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response == null) {
                return unavailable(id);
            }

            CompanyInfo info = new CompanyInfo();
            info.setId(id);
            info.setName(response.name());
            info.setNit(response.nit());
            info.setAddress(response.address());
            info.setTelephone(response.telephone());
            return info;

        } catch (Exception ex) {
            log.warn("No se pudo obtener información de la empresa {}: {}", id, ex.getMessage());
            return unavailable(id);
        }
    }

    private CompanyInfo unavailable(UUID id) {
        CompanyInfo info = new CompanyInfo();
        info.setId(id);
        info.setName("Empresa no disponible");
        info.setNit("N/A");
        info.setAddress("");
        info.setTelephone("");
        return info;
    }

    private record CompanyResponse(
            UUID id,
            String name,
            String nit,
            String address,
            String telephone
    ) {}
}
