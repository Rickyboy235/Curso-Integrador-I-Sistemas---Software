package transflow.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteCotizacionDTO {

    private Integer idCotizacion;

    private String codigoCotizacion;

    private String clienteRazonSocial;

    private String usuarioNombre;

    private String origen;

    private String destino;

    private String tipoCarga;

    private BigDecimal pesoKg;

    private String estadoCotizacion;

    private BigDecimal total;
}