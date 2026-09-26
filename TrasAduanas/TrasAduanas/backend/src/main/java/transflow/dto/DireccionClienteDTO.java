package transflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionClienteDTO {

    private Integer idDireccion;

    private Integer idCliente;

    @NotBlank
    @Size(max = 20)
    private String tipoDireccion;

    @NotBlank
    @Size(max = 200)
    private String direccion;

    @Size(max = 60)
    private String distrito;

    @Size(max = 60)
    private String provincia;
}