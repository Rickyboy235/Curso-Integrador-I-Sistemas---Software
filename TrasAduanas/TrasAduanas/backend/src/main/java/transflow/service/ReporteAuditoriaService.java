package transflow.service;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.ReporteExpedienteDTO;
import transflow.dto.ReporteIncidenciaDTO;
import transflow.model.Cotizacion;
import transflow.model.OrdenServicio;
import transflow.repository.ExpedienteDAMRepository;
import transflow.repository.IncidenciaRepository;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteAuditoriaService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ExpedienteDAMRepository expedienteDAMRepository;
    private final IncidenciaRepository incidenciaRepository;

    @Transactional(readOnly = true)
    public List<ReporteExpedienteDTO> obtenerExpedientes() {

        return expedienteDAMRepository.findAll()
                .stream()
                .map(exp -> ReporteExpedienteDTO.builder()
                        .numeroOrden(numeroOrden(exp.getOrdenServicio()))
                        .cliente(clienteDeOrden(exp.getOrdenServicio()))
                        .numeroDam(
                                exp.getNumeroDam() != null
                                        ? exp.getNumeroDam()
                                        : "Sin asignar"
                        )
                        .regimen(exp.getRegimen())
                        .canalControl(
                                exp.getCanalControl() != null
                                        ? exp.getCanalControl()
                                        : "Sin canal"
                        )
                        .estadoTramite(exp.getEstadoTramite())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteExpedientes() throws Exception {

        List<ReporteExpedienteDTO> filas = obtenerExpedientes();

        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/expedientes.jrxml");

        if (archivoJasper == null) {
            throw new RuntimeException(
                    "No se encontró el archivo expedientes.jrxml"
            );
        }

        JasperReport reporte =
                JasperCompileManager.compileReport(archivoJasper);

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(filas);

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(reporte, null, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    @Transactional(readOnly = true)
    public List<ReporteIncidenciaDTO> obtenerIncidencias() {

        return incidenciaRepository.findAll()
                .stream()
                .map(inc -> ReporteIncidenciaDTO.builder()
                        .numeroOrden(numeroOrden(inc.getOrdenServicio()))
                        .cliente(clienteDeOrden(inc.getOrdenServicio()))
                        .tipoIncidencia(inc.getTipoIncidencia())
                        .descripcion(
                                inc.getDescripcion() != null
                                        ? inc.getDescripcion()
                                        : "-"
                        )
                        .fechaReporte(
                                inc.getFechaReporte() != null
                                        ? inc.getFechaReporte().format(FORMATO_FECHA)
                                        : "-"
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteIncidencias() throws Exception {

        List<ReporteIncidenciaDTO> filas = obtenerIncidencias();

        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/incidencias.jrxml");

        if (archivoJasper == null) {
            throw new RuntimeException(
                    "No se encontró el archivo incidencias.jrxml"
            );
        }

        JasperReport reporte =
                JasperCompileManager.compileReport(archivoJasper);

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(filas);

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(reporte, null, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private String numeroOrden(OrdenServicio orden) {
        return orden != null && orden.getNumeroOrden() != null
                ? orden.getNumeroOrden()
                : "-";
    }

    private String clienteDeOrden(OrdenServicio orden) {

        if (orden == null) {
            return "-";
        }

        Cotizacion cotizacion = orden.getCotizacion();

        if (cotizacion == null
                || cotizacion.getCliente() == null
                || cotizacion.getCliente().getRazonSocial() == null) {
            return "-";
        }

        return cotizacion.getCliente().getRazonSocial();
    }
}