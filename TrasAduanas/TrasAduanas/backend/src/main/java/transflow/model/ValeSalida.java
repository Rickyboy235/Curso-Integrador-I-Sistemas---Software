package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vales_salida")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ValeSalida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vale")
    @EqualsAndHashCode.Include
    private Integer idVale;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orden", nullable = false)
    @ToString.Exclude
    private OrdenServicio ordenServicio;

    @NotBlank
    @Size(max = 20)
    @Column(name = "numero_vale", length = 20, nullable = false)
    private String numeroVale;

    @NotNull
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Builder.Default
    @OneToMany(mappedBy = "valeSalida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<DetalleVale> detalles = new ArrayList<>();

    public void emitir() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDate.now();
        }
    }

    public void addDetalle(DetalleVale detalle) {
        detalles.add(detalle);
        detalle.setValeSalida(this);
    }

    public void removeDetalle(DetalleVale detalle) {
        detalles.remove(detalle);
        detalle.setValeSalida(null);
    }
}