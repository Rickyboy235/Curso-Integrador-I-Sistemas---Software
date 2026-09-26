package transflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidacionTributosDTO {

    private Integer idLiquidacion;

    @NotNull
    private Integer idExpedienteDam;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal adValorem;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal igv;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal ipm;

    @NotBlank
    @Size(max = 20)
    private String estadoPago;

    private BigDecimal totalTributos;
}