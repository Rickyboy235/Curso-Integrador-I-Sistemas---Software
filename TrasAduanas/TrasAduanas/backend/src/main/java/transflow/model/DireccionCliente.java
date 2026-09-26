package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "direcciones_cliente")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DireccionCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    @EqualsAndHashCode.Include
    private Integer idDireccion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @ToString.Exclude
    private Cliente cliente;

    @NotBlank
    @Size(max = 20)
    @Column(name = "tipo_direccion", length = 20, nullable = false)
    private String tipoDireccion;

    @NotBlank
    @Size(max = 200)
    @Column(name = "direccion", length = 200, nullable = false)
    private String direccion;

    @Size(max = 60)
    @Column(name = "distrito", length = 60)
    private String distrito;

    @Size(max = 60)
    @Column(name = "provincia", length = 60)
    private String provincia;

    public String obtenerUbicacionCompleta() {
        StringBuilder sb = new StringBuilder();
        if (direccion != null) sb.append(direccion);
        if (distrito != null && !distrito.isBlank()) sb.append(", ").append(distrito);
        if (provincia != null && !provincia.isBlank()) sb.append(", ").append(provincia);
        return sb.toString();
    }
}