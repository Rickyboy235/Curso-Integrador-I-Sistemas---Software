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
public class ValeSalidaDTO {

    private Integer idVale;

    @NotNull
    private Integer idOrdenServicio;

    @NotBlank
    @Size(max = 20)
    private String numeroVale;

    private LocalDate fechaEmision;
}