package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vehiculos")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    @EqualsAndHashCode.Include
    private Integer idVehiculo;

    @NotBlank
    @Size(max = 10)
    @Column(name = "placa_tracto", length = 10, nullable = false, unique = true)
    private String placaTracto;

    @Size(max = 10)
    @Column(name = "placa_carreta", length = 10)
    private String placaCarreta;

    @NotNull
    @Column(name = "soat_vencimiento", nullable = false)
    private LocalDate soatVencimiento;

    @NotNull
    @Column(name = "citv_vencimiento", nullable = false)
    private LocalDate citvVencimiento;

    public boolean esAptoParaCircular() {
        LocalDate hoy = LocalDate.now();
        return soatVencimiento != null && citvVencimiento != null
                && !soatVencimiento.isBefore(hoy) && !citvVencimiento.isBefore(hoy);
    }
}