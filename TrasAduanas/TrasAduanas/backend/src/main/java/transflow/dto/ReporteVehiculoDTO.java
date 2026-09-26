package transflow.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteVehiculoDTO {

    private Integer idVehiculo;
    private String placaTracto;
    private String placaCarreta;
    private LocalDate soatVencimiento;
    private LocalDate citvVencimiento;
    private String estado;
}