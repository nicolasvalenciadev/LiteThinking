package com.litethinking.inventory.domain.port.out;

public interface EmailPort {

    void sendInventoryEmail(String to, byte[] pdfBytes);
}
