package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidencia")
    @EqualsAndHashCode.Include
    private Integer idIncidencia;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orden", nullable = false)
    @ToString.Exclude
    private OrdenServicio ordenServicio;

    @NotBlank
    @Size(max = 40)
    @Column(name = "tipo_incidencia", length = 40, nullable = false)
    private String tipoIncidencia;

    @Size(max = 255)
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @NotNull
    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    /** Fija la fecha de reporte al momento actual si aun no fue establecida. */
    public void registrar() {
        if (this.fechaReporte == null) {
            this.fechaReporte = LocalDateTime.now();
        }
    }
}