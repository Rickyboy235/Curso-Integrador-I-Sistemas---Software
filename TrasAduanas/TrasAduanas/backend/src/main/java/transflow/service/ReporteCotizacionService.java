package transflow.service;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.stereotype.Service;
import transflow.dto.ReporteCotizacionDTO;
import transflow.repository.CotizacionRepository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteCotizacionService {

    private final CotizacionRepository cotizacionRepository;

    public byte[] generarReporteCotizaciones() throws Exception {

        // 1. Obtener las cotizaciones //
        List<ReporteCotizacionDTO> cotizaciones =
        cotizacionRepository.findAll().stream()
                .map(c -> ReporteCotizacionDTO.builder()
                        .idCotizacion(c.getIdCotizacion())
                        .codigoCotizacion(c.getCodigoCotizacion())
                        .clienteRazonSocial(c.getCliente().getRazonSocial())
                        .usuarioNombre(
                                c.getUsuario().getNombresUsuario() + " " +
                                c.getUsuario().getApellidosUsuario()
                        )
                        .origen(c.getOrigen())
                        .destino(c.getDestino())
                        .tipoCarga(c.getTipoCarga())
                        .pesoKg(c.getPesoKg())
                        .estadoCotizacion(c.getEstadoCotizacion())
                        .total(c.calcularTotal())
                        .build())
                .collect(Collectors.toList());

        // 2. Cargar el archivo JRXML //
        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/cotizaciones.jrxml");

        if (archivoJasper == null) {
            throw new RuntimeException(
                    "No se encontró el archivo cotizaciones.jrxml"
            );
        }

        // 3. Compilar el reporte //
        JasperReport reporte = JasperCompileManager.compileReport(archivoJasper);

        // 4. Parámetros del reporte //
        Map<String, Object> parametros = new HashMap<>();

        // 5. Fuente de datos //
        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(cotizaciones);

        // 6. Llenar el reporte //
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                reporte,
                parametros,
                dataSource
        );

        // 7. Exportar a PDF //
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}