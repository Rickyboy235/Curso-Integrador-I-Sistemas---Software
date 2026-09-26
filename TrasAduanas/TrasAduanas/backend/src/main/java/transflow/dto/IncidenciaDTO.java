package transflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenciaDTO {

    private Integer idIncidencia;

    @NotNull
    private Integer idOrdenServicio;

    @NotBlank
    @Size(max = 40)
    private String tipoIncidencia;

    @Size(max = 255)
    private String descripcion;

    private LocalDateTime fechaReporte;
}