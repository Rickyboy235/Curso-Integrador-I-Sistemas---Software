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
import transflow.dto.ReporteOperacionDTO;
import transflow.repository.OrdenServicioRepository;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteOperacionesService {

    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public List<ReporteOperacionDTO> obtenerOperaciones() {

        return ordenServicioRepository.findAll()
                .stream()
                .map(orden -> ReporteOperacionDTO.builder()
                        .idOrden(orden.getIdOrden())
                        .numeroOrden(orden.getNumeroOrden())

                        .codigoCotizacion(
                                orden.getCotizacion() != null
                                        ? orden.getCotizacion().getCodigoCotizacion()
                                        : "Sin cotización"
                        )

                        .cliente(
                                orden.getCotizacion() != null
                                        && orden.getCotizacion().getCliente() != null
                                        ? orden.getCotizacion()
                                                .getCliente()
                                                .getRazonSocial()
                                        : "Sin cliente"
                        )

                        .vehiculo(
                                orden.getVehiculo() != null
                                        ? orden.getVehiculo().getPlacaTracto()
                                        : "Sin asignar"
                        )

                        .conductor(
                                orden.getConductor() != null
                                        ? orden.getConductor().getNombres()
                                                + " "
                                                + orden.getConductor().getApellidos()
                                        : "Sin asignar"
                        )

                        .fechaProgramada(orden.getFechaProgramada())
                        .estado(orden.getEstadoServicio())

                        .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteOperaciones() throws Exception {

        List<ReporteOperacionDTO> operaciones =
                obtenerOperaciones();

        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/operaciones.jrxml");

        if (archivoJasper == null) {
            throw new RuntimeException(
                    "No se encontró el archivo operaciones.jrxml"
            );
        }

        JasperReport reporte =
                JasperCompileManager.compileReport(archivoJasper);

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(operaciones);

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        reporte,
                        null,
                        dataSource
                );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}