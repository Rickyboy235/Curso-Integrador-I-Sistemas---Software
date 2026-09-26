package transflow.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ReporteValeSalidaDTO {

    private Integer idVale;
    private String numeroVale;
    private String numeroOrden;
    private LocalDate fechaEmision;
    private String codigoInsumo;
    private String descripcionInsumo;
    private BigDecimal cantidad;
}