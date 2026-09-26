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
public class ServicioCotizadoDTO {

    private Integer idServicio;

    private Integer idCotizacion;

    @NotBlank
    @Size(max = 200)
    private String descripcionServicio;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal cantidad;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
}