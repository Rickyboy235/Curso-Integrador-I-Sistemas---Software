package transflow.service;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.springframework.stereotype.Service;
import transflow.dto.ReporteConductorDTO;
import transflow.dto.ReporteVehiculoDTO;
import transflow.repository.ConductorRepository;
import transflow.repository.VehiculoRepository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteLogisticaService {

    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;

    public List<ReporteVehiculoDTO> obtenerVehiculos() {

        return vehiculoRepository.findAll()
                .stream()
                .map(v -> ReporteVehiculoDTO.builder()
                        .idVehiculo(v.getIdVehiculo())
                        .placaTracto(v.getPlacaTracto())
                        .placaCarreta(v.getPlacaCarreta())
                        .soatVencimiento(v.getSoatVencimiento())
                        .citvVencimiento(v.getCitvVencimiento())
                        .estado(
                                !v.getSoatVencimiento().isBefore(java.time.LocalDate.now())
                                && !v.getCitvVencimiento().isBefore(java.time.LocalDate.now())
                                        ? "Apto"
                                        : "Documentos vencidos"
                        )
                        .build())
                .collect(Collectors.toList());
    }

    public List<ReporteConductorDTO> obtenerConductores() {

        return conductorRepository.findAll()
                .stream()
                .map(c -> ReporteConductorDTO.builder()
                        .idConductor(c.getIdConductor())
                        .nombres(c.getNombres())
                        .apellidos(c.getApellidos())
                        .numDoc(c.getNumDoc())
                        .licencia(c.getLicencia())
                        .categoria(c.getCategoria())
                        .vencimientoLicencia(c.getVencimientoLicencia())
                        .estado(
                                !c.getVencimientoLicencia().isBefore(java.time.LocalDate.now())
                                        ? "Vigente"
                                        : "Vencida"
                        )
                        .build())
                .collect(Collectors.toList());
    }

    public byte[] generarReporteLogistica() throws Exception {

        List<ReporteVehiculoDTO> vehiculos = obtenerVehiculos();
        List<ReporteConductorDTO> conductores = obtenerConductores();

        InputStream archivoVehiculos = getClass()
        .getResourceAsStream("/reportes/vehiculos.jrxml");

        if (archivoVehiculos == null) {
            throw new RuntimeException("No se encontró el archivo vehiculos.jrxml"); 
        }

        JasperReport reporteVehiculos =
                JasperCompileManager.compileReport(archivoVehiculos);

        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/logistica.jrxml");

        if (archivoJasper == null) {
           throw new RuntimeException("No se encontró el archivo logistica.jrxml");
        }

        JasperReport reporte = JasperCompileManager.compileReport(archivoJasper);

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("vehiculos", vehiculos);
        parametros.put("conductores", conductores);
        parametros.put("reporteVehiculos", reporteVehiculos);

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                reporte,
                parametros,
                new JREmptyDataSource()
        );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

}