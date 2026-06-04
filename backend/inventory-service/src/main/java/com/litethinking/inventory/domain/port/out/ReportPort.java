package com.litethinking.inventory.domain.port.out;

import com.litethinking.inventory.domain.model.InventoryItem;

import java.util.List;

public interface ReportPort {

    byte[] generatePdf(List<InventoryItem> items);
}
