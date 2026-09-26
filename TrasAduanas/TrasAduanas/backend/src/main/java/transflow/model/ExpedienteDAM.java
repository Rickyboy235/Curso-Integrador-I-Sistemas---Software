package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "expedientes_dam")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExpedienteDAM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_expediente")
    @EqualsAndHashCode.Include
    private Integer idExpediente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orden", nullable = false)
    @ToString.Exclude
    private OrdenServicio ordenServicio;

    @Size(max = 20)
    @Column(name = "numero_dam", length = 20)
    private String numeroDam;

    @NotBlank
    @Size(max = 40)
    @Column(name = "regimen", length = 40, nullable = false)
    private String regimen;

    @Size(max = 10)
    @Column(name = "canal_control", length = 10)
    private String canalControl;

    @NotBlank
    @Size(max = 20)
    @Column(name = "estado_tramite", length = 20, nullable = false)
    private String estadoTramite;

    public void actualizarCanal(String canal) {
        this.canalControl = canal;
    }
}