package com.litethinking.inventory.domain.port.in;

import com.litethinking.inventory.domain.model.InventoryItem;

import java.util.List;

public interface InventoryUseCase {

    List<InventoryItem> getInventory();

    byte[] generateInventoryPdf();

    void sendPdfByEmail(String email);
}
