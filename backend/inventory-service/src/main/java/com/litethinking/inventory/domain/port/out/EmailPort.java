package com.litethinking.inventory.domain.port.out;

public interface EmailPort {

    void sendEmail(String to, byte[] pdfBytes);
}
