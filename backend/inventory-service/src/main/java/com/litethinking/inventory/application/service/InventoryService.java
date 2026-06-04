package com.litethinking.inventory.application.service;

import com.litethinking.inventory.domain.model.CompanyInfo;
import com.litethinking.inventory.domain.model.InventoryItem;
import com.litethinking.inventory.domain.port.in.InventoryUseCase;
import com.litethinking.inventory.domain.port.out.CompanyQueryPort;
import com.litethinking.inventory.domain.port.out.EmailPort;
import com.litethinking.inventory.domain.port.out.ProductQueryPort;
import com.litethinking.inventory.domain.port.out.ReportPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class InventoryService implements InventoryUseCase {

    private final ProductQueryPort productQueryPort;
    private final CompanyQueryPort companyQueryPort;
    private final ReportPort reportPort;
    private final EmailPort emailPort;

    public InventoryService(ProductQueryPort productQueryPort,
                            CompanyQueryPort companyQueryPort,
                            ReportPort reportPort,
                            EmailPort emailPort) {
        this.productQueryPort = productQueryPort;
        this.companyQueryPort = companyQueryPort;
        this.reportPort = reportPort;
        this.emailPort = emailPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItem> getInventory() {
        List<InventoryItem> items = new ArrayList<>(productQueryPort.fetchProducts());

        Map<UUID, CompanyInfo> companyCache = new HashMap<>();

        items.stream()
                .map(InventoryItem::getCompanyId)
                .distinct()
                .forEach(companyId -> {
                    CompanyInfo info = companyQueryPort.fetchCompany(companyId);
                    companyCache.put(companyId, info);
                });

        items.forEach(item -> {
            CompanyInfo info = companyCache.get(item.getCompanyId());
            if (info != null) {
                item.setCompanyName(info.getName());
                item.setCompanyNit(info.getNit());
            }
        });

        items.sort(Comparator
                .comparing(InventoryItem::getCompanyName,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(InventoryItem::getProductCode,
                        Comparator.nullsLast(Comparator.naturalOrder())));

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateInventoryPdf() {
        List<InventoryItem> items = getInventory();
        return reportPort.generateInventoryPdf(items);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendPdfByEmail(String email) {
        byte[] pdfBytes = generateInventoryPdf();
        emailPort.sendInventoryEmail(email, pdfBytes);
    }
}
