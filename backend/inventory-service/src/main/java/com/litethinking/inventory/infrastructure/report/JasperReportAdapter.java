package com.litethinking.inventory.infrastructure.report;

import com.litethinking.inventory.domain.exception.ReportGenerationException;
import com.litethinking.inventory.domain.model.InventoryItem;
import com.litethinking.inventory.domain.port.out.ReportPort;
import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JasperReportAdapter implements ReportPort {

    private static final Logger log = LoggerFactory.getLogger(JasperReportAdapter.class);
    private static final String TEMPLATE_PATH = "/templates/inventory_report.jrxml";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private JasperReport compiledReport;

    @PostConstruct
    public void compileTemplate() {
        try (InputStream stream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (stream == null) {
                throw new ReportGenerationException(
                        "No se encontró la plantilla del reporte: " + TEMPLATE_PATH);
            }
            compiledReport = JasperCompileManager.compileReport(stream);
            log.info("Plantilla JasperReports compilada exitosamente.");
        } catch (ReportGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new ReportGenerationException(
                    "Error al compilar la plantilla del reporte de inventario.", e);
        }
    }

    @Override
    public byte[] generatePdf(List<InventoryItem> items) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_DATE",
                    LocalDateTime.now().format(DATE_FORMATTER));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    compiledReport, parameters, dataSource);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteOut));
            exporter.exportReport();

            byte[] pdfBytes = byteOut.toByteArray();

            savePdfToDisk(pdfBytes);

            return pdfBytes;

        } catch (ReportGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new ReportGenerationException(
                    "Error al generar el reporte de inventario en formato PDF.", e);
        }
    }

    private void savePdfToDisk(byte[] pdfBytes) {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "inventory_report.pdf");
        try (OutputStream out = Files.newOutputStream(path)) {
            out.write(pdfBytes);
            log.info("Reporte PDF guardado en: {}", path.toAbsolutePath());
        } catch (Exception e) {
            log.warn("No se pudo guardar el reporte PDF en disco: {}", e.getMessage());
        }
    }
}
