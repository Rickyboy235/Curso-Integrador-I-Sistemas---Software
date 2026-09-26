package transflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoClienteDTO {

    private Integer idContacto;

    private Integer idCliente;

    @NotBlank
    @Size(max = 80)
    private String nombres;

    @NotBlank
    @Size(max = 80)
    private String apellidos;

    @Size(max = 60)
    private String cargo;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String telefono;

    @NotNull
    private Boolean esPrincipal;
}