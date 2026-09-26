package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "conductores")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conductor")
    @EqualsAndHashCode.Include
    private Integer idConductor;

    @NotBlank
    @Size(max = 15)
    @Column(name = "num_doc", length = 15, nullable = false, unique = true)
    private String numDoc;

    @NotBlank
    @Size(max = 80)
    @Column(name = "nombres", length = 80, nullable = false)
    private String nombres;

    @NotBlank
    @Size(max = 80)
    @Column(name = "apellidos", length = 80, nullable = false)
    private String apellidos;

    @NotBlank
    @Size(max = 15)
    @Column(name = "licencia", length = 15, nullable = false, unique = true)
    private String licencia;

    @NotBlank
    @Size(max = 5)
    @Column(name = "categoria", length = 5, nullable = false)
    private String categoria;

    @NotNull
    @Column(name = "vencimiento_licencia", nullable = false)
    private LocalDate vencimientoLicencia;

    public boolean licenciaVigente() {
        return vencimientoLicencia != null && !vencimientoLicencia.isBefore(LocalDate.now());
    }
}