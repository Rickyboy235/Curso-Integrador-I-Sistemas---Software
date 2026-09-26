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
import transflow.dto.ReporteValeSalidaDTO;
import transflow.repository.ValeSalidaRepository;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteProduccionService {

    private final ValeSalidaRepository valeSalidaRepository;

    @Transactional(readOnly = true)
    public List<ReporteValeSalidaDTO> obtenerVales() {

        return valeSalidaRepository.findAll()
                .stream()
                .flatMap(vale -> vale.getDetalles()
                        .stream()
                        .map(detalle -> ReporteValeSalidaDTO.builder()
                                .idVale(vale.getIdVale())
                                .numeroVale(vale.getNumeroVale())
                                .numeroOrden(
                                        vale.getOrdenServicio().getNumeroOrden()
                                )
                                .fechaEmision(vale.getFechaEmision())
                                .codigoInsumo(
                                        detalle.getInsumo().getCodigo()
                                )
                                .descripcionInsumo(
                                        detalle.getInsumo().getDescripcion()
                                )
                                .cantidad(detalle.getCantidad())
                                .build()
                        )
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteProduccion() throws Exception {

        List<ReporteValeSalidaDTO> vales = obtenerVales();

        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/produccion.jrxml");

        if (archivoJasper == null) {
            throw new RuntimeException(
                    "No se encontró el archivo produccion.jrxml"
            );
        }

        JasperReport reporte =
                JasperCompileManager.compileReport(archivoJasper);

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(vales);

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        reporte,
                        null,
                        dataSource
                );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}