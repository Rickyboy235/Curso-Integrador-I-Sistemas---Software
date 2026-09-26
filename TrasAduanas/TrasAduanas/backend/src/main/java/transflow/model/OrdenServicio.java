package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ordenes_servicio")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrdenServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    @EqualsAndHashCode.Include
    private Integer idOrden;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cotizacion", nullable = false)
    @ToString.Exclude
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo")
    @ToString.Exclude
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conductor")
    @ToString.Exclude
    private Conductor conductor;

    @NotBlank
    @Size(max = 20)
    @Column(name = "numero_orden", length = 20, nullable = false)
    private String numeroOrden;

    @NotNull
    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @NotBlank
    @Size(max = 20)
    @Column(name = "estado", length = 20, nullable = false)
    private String estadoServicio;

    public void iniciarServicio() {
        this.estadoServicio = "EN_PROCESO";
    }

    public void finalizarServicio() {
        this.estadoServicio = "FINALIZADO";
    }
}