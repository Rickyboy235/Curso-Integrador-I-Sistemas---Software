package transflow.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ReporteInsumoDTO {

    private Integer idInsumo;
    private String codigo;
    private String descripcion;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private String estado;
}