package transflow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReporteDireccionDTO {

    private String tipo; // "USUARIO" o "ROL"

    // --- columnas de la sección USUARIOS --- //
    private String nombreCompleto;
    private String dni;
    private String email;
    private String rolAsignado;
    private String estadoTexto;

    // --- columnas de la sección ROLES --- //
    private String nombreRol;
    private String descripcionRol;
    private String permisosRol;
}