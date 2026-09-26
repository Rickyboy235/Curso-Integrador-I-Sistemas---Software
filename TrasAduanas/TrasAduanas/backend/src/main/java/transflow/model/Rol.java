package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @EqualsAndHashCode.Include
    private Integer idRol;

    @NotBlank
    @Size(max = 50)
    @Column(name = "nombre_rol", length = 50, nullable = false)
    private String nombreRol;

    @Size(max = 200)
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    public boolean esAdministrador() {
        return nombreRol != null && nombreRol.trim().equalsIgnoreCase("Administrador");
    }

    public List<String> obtenerPermisos() {
        if (esAdministrador()) {
            return List.of("LECTURA", "ESCRITURA", "ADMINISTRACION");
        }
        return List.of("LECTURA", "ESCRITURA");
    }
}