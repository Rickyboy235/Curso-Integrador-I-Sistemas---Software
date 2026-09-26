package transflow.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteConductorDTO {

    private Integer idConductor;
    private String nombres;
    private String apellidos;
    private String numDoc;
    private String licencia;
    private String categoria;
    private LocalDate vencimientoLicencia;
    private String estado;
}