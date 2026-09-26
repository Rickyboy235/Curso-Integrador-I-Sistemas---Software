package transflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private Integer idUsuario;

    @NotNull
    private Integer idRol;

    private String rolNombre;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{8}")
    private String dni;

    @NotBlank
    @Size(max = 80)
    private String nombresUsuario;

    @NotBlank
    @Size(max = 80)
    private String apellidosUsuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 100)
    private String password;

    private Boolean estadoUsuario;
}