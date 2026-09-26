package transflow.service;

import lombok.RequiredArgsConstructor;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.stereotype.Service;

import transflow.dto.ReporteInsumoDTO;
import transflow.repository.InsumoRepository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteAlmacenService {

    private final InsumoRepository insumoRepository;

    public byte[] generarReporteAlmacen() throws Exception {

        // 1. Obtener los insumos
        List<ReporteInsumoDTO> insumos =
                insumoRepository.findAll()
                        .stream()
                        .map(i -> ReporteInsumoDTO.builder()
                                .idInsumo(i.getIdInsumo())
                                .codigo(i.getCodigo())
                                .descripcion(i.getDescripcion())
                                .stockActual(i.getStockActual())
                                .stockMinimo(i.getStockMinimo())
                                .estado(
                                        i.requiereReposicion()
                                                ? "Reponer"
                                                : "OK"
                                )
                                .build())
                        .collect(Collectors.toList());

        // 2. Cargar el archivo JRXML
        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/almacen.jrxml");

        if (archivoJasper == null) {
            throw new RuntimeException(
                    "No se encontró el archivo almacen.jrxml"
            );
        }

        // 3. Compilar el reporte
        JasperReport reporte =
                JasperCompileManager.compileReport(archivoJasper);

        // 4. Parámetros
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(
                "tituloReporte",
                "Reporte de Inventario de Almacén"
        );

        // 5. Fuente de datos
        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(insumos);

        // 6. Llenar el reporte
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        reporte,
                        parametros,
                        dataSource
                );

        // 7. Exportar a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}