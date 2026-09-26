package transflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculoDTO {

    private Integer idVehiculo;

    @NotBlank
    @Size(max = 10)
    private String placaTracto;

    @Size(max = 10)
    private String placaCarreta;

    @NotNull
    private LocalDate soatVencimiento;

    @NotNull
    private LocalDate citvVencimiento;
}