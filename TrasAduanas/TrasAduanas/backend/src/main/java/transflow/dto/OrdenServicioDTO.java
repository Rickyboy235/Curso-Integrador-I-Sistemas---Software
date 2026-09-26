package transflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenServicioDTO {

    private Integer idOrden;

    @NotNull
    private Integer idCotizacion;

    private Integer idVehiculo;

    private Integer idConductor;

    @NotBlank
    @Size(max = 20)
    private String numeroOrden;

    @NotNull
    private LocalDate fechaProgramada;

    @NotBlank
    @Size(max = 20)
    private String estadoServicio;
}