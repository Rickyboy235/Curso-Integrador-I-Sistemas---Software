package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Integer idUsuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    @ToString.Exclude
    private Rol rol;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{8}")
    @Column(name = "dni", length = 8, nullable = false, unique = true)
    private String dni;

    @NotBlank
    @Size(max = 80)
    @Column(name = "nombres", length = 80, nullable = false)
    private String nombresUsuario;

    @NotBlank
    @Size(max = 80)
    @Column(name = "apellidos", length = 80, nullable = false)
    private String apellidosUsuario;

    @NotBlank
    @Size(max = 255)
    @ToString.Exclude
    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estadoUsuario;

    public boolean autenticar(String passwordHashRecibido) {
        return estaActivo() && passwordHash != null && passwordHash.equals(passwordHashRecibido);
    }

    public boolean estaActivo() {
        return Boolean.TRUE.equals(estadoUsuario);
    }
}