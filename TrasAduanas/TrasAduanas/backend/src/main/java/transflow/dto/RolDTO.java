package transflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolDTO {

    private Integer idRol;

    @NotBlank
    @Size(max = 50)
    private String nombreRol;

    @Size(max = 200)
    private String descripcion;
}