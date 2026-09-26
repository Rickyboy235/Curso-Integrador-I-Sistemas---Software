package transflow.service;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.ReporteDireccionDTO;
import transflow.repository.RolRepository;
import transflow.repository.UsuarioRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteDireccionService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<ReporteDireccionDTO> obtenerDireccion() {

        List<ReporteDireccionDTO> filas = new ArrayList<>();

        // Primero todas las filas de USUARIO, luego todas las de ROL:
        // el grupo del .jrxml corta por "tipo", así que deben ir juntas.
        filas.addAll(
                usuarioRepository.findAll()
                        .stream()
                        .map(usuario -> ReporteDireccionDTO.builder()
                                .tipo("USUARIO")
                                .nombreCompleto(
                                        usuario.getNombresUsuario()
                                                + " "
                                                + usuario.getApellidosUsuario()
                                )
                                .dni(usuario.getDni())
                                .email(usuario.getEmail())
                                .rolAsignado(
                                        usuario.getRol() != null
                                                ? usuario.getRol().getNombreRol()
                                                : "Sin rol"
                                )
                                .estadoTexto(
                                        usuario.estaActivo() ? "Activo" : "Inactivo"
                                )
                                .build()
                        )
                        .collect(Collectors.toList())
        );

        filas.addAll(
                rolRepository.findAll()
                        .stream()
                        .map(rol -> ReporteDireccionDTO.builder()
                                .tipo("ROL")
                                .nombreRol(rol.getNombreRol())
                                .descripcionRol(
                                        rol.getDescripcion() != null
                                                ? rol.getDescripcion()
                                                : "-"
                                )
                                .permisosRol(
                                        String.join(", ", rol.obtenerPermisos())
                                )
                                .build()
                        )
                        .collect(Collectors.toList())
        );

        return filas;
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteDireccion() throws Exception {

        List<ReporteDireccionDTO> filas = obtenerDireccion();

        InputStream archivoJasper = getClass()
                .getResourceAsStream("/reportes/direccion.jrxml");

        if (archivoJasper == null) {
            throw new RuntimeException(
                    "No se encontró el archivo direccion.jrxml"
            );
        }

        JasperReport reporte =
                JasperCompileManager.compileReport(archivoJasper);

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(filas);

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        reporte,
                        null,
                        dataSource
                );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}