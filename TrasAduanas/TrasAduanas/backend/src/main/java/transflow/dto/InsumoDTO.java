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
public class InsumoDTO {

    private Integer idInsumo;

    @NotBlank
    @Size(max = 20)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal stockActual;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal stockMinimo;
}