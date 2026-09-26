package transflow.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReporteOperacionDTO {

    private Integer idOrden;
    private String numeroOrden;
    private String codigoCotizacion;
    private String cliente;
    private String vehiculo;
    private String conductor;
    private LocalDate fechaProgramada;
    private String estado;
}