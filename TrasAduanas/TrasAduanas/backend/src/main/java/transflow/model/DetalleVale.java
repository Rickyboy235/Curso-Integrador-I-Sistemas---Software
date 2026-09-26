package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_vales")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVale {

    @Builder.Default
    @EmbeddedId
    private DetalleValeId id = new DetalleValeId();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idVale")
    @JoinColumn(name = "id_vale", nullable = false)
    @ToString.Exclude
    private ValeSalida valeSalida;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idInsumo")
    @JoinColumn(name = "id_insumo", nullable = false)
    @ToString.Exclude
    private Insumo insumo;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    /** El diagrama define el retorno como int; se redondea aqui desde el DECIMAL fisico. */
    public int obtenerSubcantidad() {
        return cantidad == null ? 0 : cantidad.intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleVale that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}