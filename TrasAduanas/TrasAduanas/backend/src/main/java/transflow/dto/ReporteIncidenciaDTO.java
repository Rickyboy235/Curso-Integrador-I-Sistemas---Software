package transflow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReporteIncidenciaDTO {

    private String numeroOrden;
    private String cliente;
    private String tipoIncidencia;
    private String descripcion;
    private String fechaReporte;
}