package transflow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReporteExpedienteDTO {

    private String numeroOrden;
    private String cliente;
    private String numeroDam;
    private String regimen;
    private String canalControl;
    private String estadoTramite;
}