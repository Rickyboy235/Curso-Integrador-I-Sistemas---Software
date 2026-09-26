package transflow.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteLiquidacionDTO {

    private Integer idLiquidacion;

    private String numeroOrden;

    private String numeroDam;

    private String regimen;

    private BigDecimal adValorem;

    private BigDecimal igv;

    private BigDecimal ipm;

    private BigDecimal totalTributos;

    private String estadoPago;

}