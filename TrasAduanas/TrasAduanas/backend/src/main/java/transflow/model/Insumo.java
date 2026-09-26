package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "insumos")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    @EqualsAndHashCode.Include
    private Integer idInsumo;

    @NotBlank
    @Size(max = 20)
    @Column(name = "codigo", length = 20, nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "descripcion", length = 150, nullable = false)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "stock_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockActual;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "stock_minimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockMinimo;

    public boolean requiereReposicion() {
        return stockActual != null && stockMinimo != null && stockActual.compareTo(stockMinimo) <= 0;
    }

    public void descontarStock(BigDecimal cantidad) {
        if (cantidad == null || stockActual == null) {
            return;
        }
        this.stockActual = stockActual.subtract(cantidad);
    }
}