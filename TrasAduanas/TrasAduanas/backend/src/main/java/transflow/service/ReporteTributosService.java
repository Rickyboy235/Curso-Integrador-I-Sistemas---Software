package transflow.service;

import lombok.RequiredArgsConstructor;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import transflow.dto.ReporteLiquidacionDTO;
import transflow.repository.LiquidacionTributosRepository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteTributosService {

    private final LiquidacionTributosRepository liquidacionTributosRepository;

    public byte[] generarReporteLiquidacionesPdf() {

        List<ReporteLiquidacionDTO> filas = liquidacionTributosRepository.findAll().stream()
                .map(l -> ReporteLiquidacionDTO.builder()
                        .idLiquidacion(l.getIdLiquidacion())
                        .numeroOrden(l.getExpedienteDam().getOrdenServicio().getNumeroOrden())
                        .numeroDam(l.getExpedienteDam().getNumeroDam())
                        .regimen(l.getExpedienteDam().getRegimen())
                        .adValorem(l.getAdValorem())
                        .igv(l.getIgv())
                        .ipm(l.getIpm())
                        .totalTributos(l.calcularTotalTributos())
                        .estadoPago(l.getEstadoPago())
                        .build())
                .collect(Collectors.toList());

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(filas);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tituloReporte", "Reporte de Liquidación de Tributos");

        try (InputStream plantilla = new ClassPathResource(
                "reportes/liquidacion_tributos.jrxml"
        ).getInputStream()) {

            JasperReport reporteCompilado =
                    JasperCompileManager.compileReport(plantilla);

            JasperPrint reporteLleno =
                    JasperFillManager.fillReport(
                            reporteCompilado,
                            parametros,
                            dataSource
                    );

            return JasperExportManager.exportReportToPdf(reporteLleno);

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "No se pudo generar el reporte de tributos. "
                    + "Causa: "
                    + e.getClass().getName()
                    + " - "
                    + e.getMessage(),
                    e
            );
        }
    }
}