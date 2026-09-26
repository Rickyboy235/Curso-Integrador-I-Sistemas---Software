package transflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleValeDTO {

    private Integer idVale;

    @NotNull
    private Integer idInsumo;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal cantidad;
}