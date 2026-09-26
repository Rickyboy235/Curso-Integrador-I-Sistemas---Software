package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "contactos_cliente")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContactoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto")
    @EqualsAndHashCode.Include
    private Integer idContacto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @ToString.Exclude
    private Cliente cliente;

    @NotBlank
    @Size(max = 80)
    @Column(name = "nombres", length = 80, nullable = false)
    private String nombres;

    @NotBlank
    @Size(max = 80)
    @Column(name = "apellidos", length = 80, nullable = false)
    private String apellidos;

    @Size(max = 60)
    @Column(name = "cargo", length = 60)
    private String cargo;

    @Email
    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @NotNull
    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal;

    public void marcarComoPrincipal() {
        this.esPrincipal = true;
    }
}