package transflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteDAMDTO {

    private Integer idExpediente;

    @NotNull
    private Integer idOrdenServicio;

    @Size(max = 20)
    private String numeroDam;

    @NotBlank
    @Size(max = 40)
    private String regimen;

    @Size(max = 10)
    private String canalControl;

    @NotBlank
    @Size(max = 20)
    private String estadoTramite;
}