package transflow.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import transflow.service.ReporteTributosService;
import transflow.service.ReporteAlmacenService;
import transflow.service.ReporteCotizacionService;
import transflow.service.ReporteLogisticaService;
import transflow.service.ReporteProduccionService;
import transflow.service.ReporteOperacionesService;
import transflow.service.ReporteDireccionService;
import transflow.service.ReporteAuditoriaService;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteTributosService reporteTributosService;
    private final ReporteCotizacionService reporteCotizacionService;
    private final ReporteLogisticaService reporteLogisticaService;
    private final ReporteAlmacenService reporteAlmacenService;
    private final ReporteProduccionService reporteProduccionService;
    private final ReporteOperacionesService reporteOperacionesService;
    private final ReporteDireccionService reporteDireccionService;
    private final ReporteAuditoriaService reporteAuditoriaService;
    

    
    @GetMapping(
        value = "/tributos/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> reporteTributosPdf() {

        byte[] pdf = reporteTributosService
                .generarReporteLiquidacionesPdf();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"reporte_marketing.pdf\""
                )
                .contentLength(pdf.length)
                .body(pdf);
    }

    
    @GetMapping(
        value = "/cotizaciones/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> reporteCotizacionesPdf() {

        try {
            byte[] pdf = reporteCotizacionService
                    .generarReporteCotizaciones();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"reporte_cotizaciones.pdf\""
                    )
                    .contentLength(pdf.length)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    
    @GetMapping(
        value = "/logistica/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> reporteLogisticaPdf() {
        try {
            byte[] pdf = reporteLogisticaService
                    .generarReporteLogistica();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"reporte_logistica.pdf\""
                    )
                    .contentLength(pdf.length)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/almacen/pdf")
    public ResponseEntity<byte[]> generarReporteAlmacen() {

        try {

            byte[] pdf = reporteAlmacenService.generarReporteAlmacen();

            return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=reporte-almacen.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                .body(("ERROR: " + e.getMessage()).getBytes());
          }
    }

    @GetMapping("/produccion/pdf")
    public ResponseEntity<byte[]> generarReporteProduccion() {

        try {

            byte[] pdf =
                reporteProduccionService.generarReporteProduccion();

            return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=reporte-produccion.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                .build();
          }
    }

    @GetMapping("/operaciones/pdf")
    public ResponseEntity<byte[]> generarReporteOperaciones() {

        try {

            byte[] pdf =
                reporteOperacionesService.generarReporteOperaciones();

            return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=reporte-operaciones.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                .build();
          }
    }

    @GetMapping("/direccion/pdf")
    public ResponseEntity<byte[]> generarReporteDireccion() {

        try {

            byte[] pdf =
                reporteDireccionService.generarReporteDireccion();

            return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=reporte-direccion.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                .build();
          }
    }

        @GetMapping("/auditoria/expedientes/pdf")
    public ResponseEntity<byte[]> generarReporteExpedientes() {

        try {

            byte[] pdf =
                reporteAuditoriaService.generarReporteExpedientes();

            return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=reporte-expedientes.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                .build();
          }
    }

    @GetMapping("/auditoria/incidencias/pdf")
    public ResponseEntity<byte[]> generarReporteIncidencias() {

        try {

            byte[] pdf =
                reporteAuditoriaService.generarReporteIncidencias();

            return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=reporte-incidencias.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                .build();
          }
    }
}