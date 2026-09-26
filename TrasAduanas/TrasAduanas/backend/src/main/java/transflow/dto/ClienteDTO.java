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
public class ClienteDTO {

    private Integer idCliente;

    @NotBlank
    @Size(max = 10)
    private String tipoDoc;

    @NotBlank
    @Size(max = 15)
    private String numDoc;

    @NotBlank
    @Size(max = 150)
    private String razonSocial;

    @Email
    @Size(max = 100)
    private String emailPrincipal;

    @NotNull
    private Boolean estadoCliente;
}