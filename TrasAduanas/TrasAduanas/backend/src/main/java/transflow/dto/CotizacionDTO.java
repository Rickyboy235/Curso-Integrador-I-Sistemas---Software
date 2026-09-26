package transflow.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotizacionDTO {

    private Integer idCotizacion;

    @NotNull
    private Integer idCliente;

    private String clienteRazonSocial;

    @NotNull
    private Integer idUsuario;

    private String usuarioNombre;

    private Integer idDireccionEntrega;

    private String codigoCotizacion;

    @NotBlank
    @Size(max = 100)
    private String origen;

    @NotBlank
    @Size(max = 100)
    private String destino;

    @NotBlank
    @Size(max = 40)
    private String tipoCarga;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal pesoKg;

    private String estadoCotizacion;

    @Builder.Default
    @Valid
    private List<ServicioCotizadoDTO> servicios = new ArrayList<>();

    private BigDecimal total;
}