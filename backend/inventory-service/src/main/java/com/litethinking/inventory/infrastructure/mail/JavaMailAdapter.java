package com.litethinking.inventory.infrastructure.mail;

import com.litethinking.inventory.domain.exception.EmailDeliveryException;
import com.litethinking.inventory.domain.port.out.EmailPort;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class JavaMailAdapter implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(JavaMailAdapter.class);
    private static final String SUBJECT = "Reporte de Inventario - LiteThinking";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;

    public JavaMailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, byte[] pdfBytes) {
        File pdfFile = Paths.get(System.getProperty("java.io.tmpdir"), "inventory_report.pdf")
                .toFile();

        if (!pdfFile.exists()) {
            throw new EmailDeliveryException(
                    "No se encontró el archivo PDF del reporte para enviar por correo electrónico.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(SUBJECT);
            helper.setText(buildEmailBody(), true);
            helper.addAttachment("inventory_report.pdf", pdfFile);

            mailSender.send(message);
            log.info("Reporte enviado correctamente a: {}", to);

        } catch (Exception e) {
            throw new EmailDeliveryException(
                    "Error al enviar el reporte de inventario por correo electrónico: "
                            + e.getMessage(), e);
        }
    }

    private String buildEmailBody() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        return "<html><body>" +
               "Estimado usuario,<br><br>" +
               "Adjunto encontrará el reporte de inventario generado el " + date + ".<br><br>" +
               "Atentamente,<br>LiteThinking S.A.S" +
               "</body></html>";
    }
}
