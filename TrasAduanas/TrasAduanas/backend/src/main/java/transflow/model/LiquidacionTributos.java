package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "liquidaciones_tributos")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LiquidacionTributos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_liquidacion")
    @EqualsAndHashCode.Include
    private Integer idLiquidacion;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_expediente", nullable = false, unique = true)
    @ToString.Exclude
    private ExpedienteDAM expedienteDam;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "ad_valorem", nullable = false, precision = 12, scale = 2)
    private BigDecimal adValorem;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "igv", nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "ipm", nullable = false, precision = 12, scale = 2)
    private BigDecimal ipm;

    @NotBlank
    @Size(max = 20)
    @Column(name = "estado_pago", length = 20, nullable = false)
    private String estadoPago;

    public BigDecimal calcularTotalTributos() {
        BigDecimal total = BigDecimal.ZERO;
        if (adValorem != null) total = total.add(adValorem);
        if (igv != null) total = total.add(igv);
        if (ipm != null) total = total.add(ipm);
        return total;
    }
}