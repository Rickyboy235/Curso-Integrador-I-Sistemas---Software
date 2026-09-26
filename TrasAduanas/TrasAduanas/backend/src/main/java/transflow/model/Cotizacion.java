package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cotizaciones")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    @EqualsAndHashCode.Include
    private Integer idCotizacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @ToString.Exclude
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    /** No esta en el diagrama de clases, pero existe como FK opcional en la tabla fisica. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direccion_entrega")
    @ToString.Exclude
    private DireccionCliente direccionEntrega;

    @NotBlank
    @Size(max = 20)
    @Column(name = "codigo_cotizacion", length = 20, nullable = false)
    private String codigoCotizacion;

    @NotBlank
    @Size(max = 100)
    @Column(name = "origen", length = 100, nullable = false)
    private String origen;

    @NotBlank
    @Size(max = 100)
    @Column(name = "destino", length = 100, nullable = false)
    private String destino;

    @NotBlank
    @Size(max = 40)
    @Column(name = "tipo_carga", length = 40, nullable = false)
    private String tipoCarga;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "peso_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoKg;

    @NotBlank
    @Size(max = 20)
    @Column(name = "estado", length = 20, nullable = false)
    private String estadoCotizacion;

    @Builder.Default
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ServicioCotizado> servicios = new ArrayList<>();

    /** Suma los subtotales de todos los servicios cotizados. */
    public BigDecimal calcularTotal() {
        return servicios.stream()
                .map(ServicioCotizado::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estadoCotizacion = nuevoEstado;
    }

    public void addServicio(ServicioCotizado servicio) {
        servicios.add(servicio);
        servicio.setCotizacion(this);
    }

    public void removeServicio(ServicioCotizado servicio) {
        servicios.remove(servicio);
        servicio.setCotizacion(null);
    }
}