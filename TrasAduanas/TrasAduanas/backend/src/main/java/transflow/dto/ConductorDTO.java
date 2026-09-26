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
public class ConductorDTO {

    private Integer idConductor;

    @NotBlank
    @Size(max = 15)
    private String numDoc;

    @NotBlank
    @Size(max = 80)
    private String nombres;

    @NotBlank
    @Size(max = 80)
    private String apellidos;

    @NotBlank
    @Size(max = 15)
    private String licencia;

    @NotBlank
    @Size(max = 5)
    private String categoria;

    @NotNull
    private LocalDate vencimientoLicencia;
}