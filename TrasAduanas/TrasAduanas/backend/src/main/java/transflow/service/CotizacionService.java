package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.CotizacionDTO;
import transflow.dto.ServicioCotizadoDTO;
import transflow.model.Cliente;
import transflow.model.Cotizacion;
import transflow.model.DireccionCliente;
import transflow.model.ServicioCotizado;
import transflow.model.Usuario;
import transflow.repository.ClienteRepository;
import transflow.repository.CotizacionRepository;
import transflow.repository.DireccionClienteRepository;
import transflow.repository.UsuarioRepository;
import transflow.repository.OrdenServicioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionClienteRepository direccionClienteRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public List<CotizacionDTO> listarTodos() {
        return cotizacionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CotizacionDTO obtenerPorId(Integer id) {
        return toDTO(buscar(id));
    }

    @Transactional
    public CotizacionDTO crear(CotizacionDTO dto) {
        Cotizacion cotizacion = new Cotizacion();
        aplicarDatos(cotizacion, dto);

        cotizacion.setEstadoCotizacion(
                (dto.getEstadoCotizacion() == null || dto.getEstadoCotizacion().isBlank())
                        ? "PENDIENTE" : dto.getEstadoCotizacion());

        cotizacion.setCodigoCotizacion("TEMP");
        reemplazarServicios(cotizacion, dto.getServicios());

        Cotizacion guardada = cotizacionRepository.save(cotizacion);

        guardada.setCodigoCotizacion(
                (dto.getCodigoCotizacion() == null || dto.getCodigoCotizacion().isBlank())
                        ? "COT-" + String.format("%04d", guardada.getIdCotizacion())
                        : dto.getCodigoCotizacion());

        return toDTO(cotizacionRepository.save(guardada));
    }

    @Transactional
    public CotizacionDTO actualizar(Integer id, CotizacionDTO dto) {
        Cotizacion cotizacion = buscar(id);
        aplicarDatos(cotizacion, dto);

        if (dto.getEstadoCotizacion() != null && !dto.getEstadoCotizacion().isBlank()) {
            cotizacion.setEstadoCotizacion(dto.getEstadoCotizacion());
        }
        if (dto.getCodigoCotizacion() != null && !dto.getCodigoCotizacion().isBlank()) {
            cotizacion.setCodigoCotizacion(dto.getCodigoCotizacion());
        }

        reemplazarServicios(cotizacion, dto.getServicios());

        return toDTO(cotizacionRepository.save(cotizacion));
    }

    @Transactional
    public CotizacionDTO cambiarEstado(Integer id, String nuevoEstado) {
        Cotizacion cotizacion = buscar(id);
        cotizacion.cambiarEstado(nuevoEstado);
        return toDTO(cotizacionRepository.save(cotizacion));
    }

    @Transactional
    public void eliminar(Integer id) {

        if (!cotizacionRepository.existsById(id)) {
            throw new RuntimeException(
                "Cotización no encontrada con id: " + id
            );
        }

        if (!ordenServicioRepository
            .findByCotizacion_IdCotizacion(id)
            .isEmpty()) {

            throw new RuntimeException(
                "No se puede eliminar la cotización porque está asociada a una o más órdenes de servicio."
            );
        }

        cotizacionRepository.deleteById(id);
    }

    private Cotizacion buscar(Integer id) {
        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotizacion no encontrada con id: " + id));
    }

    private void aplicarDatos(Cotizacion cotizacion, CotizacionDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + dto.getIdCliente()));
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getIdUsuario()));

        cotizacion.setCliente(cliente);
        cotizacion.setUsuario(usuario);

        if (dto.getIdDireccionEntrega() != null) {
            DireccionCliente direccion = direccionClienteRepository.findById(dto.getIdDireccionEntrega())
                    .orElseThrow(() -> new RuntimeException("Direccion no encontrada con id: " + dto.getIdDireccionEntrega()));
            cotizacion.setDireccionEntrega(direccion);
        } else {
            cotizacion.setDireccionEntrega(null);
        }

        cotizacion.setOrigen(dto.getOrigen());
        cotizacion.setDestino(dto.getDestino());
        cotizacion.setTipoCarga(dto.getTipoCarga());
        cotizacion.setPesoKg(dto.getPesoKg());
    }

    private void reemplazarServicios(Cotizacion cotizacion, List<ServicioCotizadoDTO> serviciosDTO) {
        cotizacion.getServicios().clear();

        if (serviciosDTO == null) {
            return;
        }

        for (ServicioCotizadoDTO s : serviciosDTO) {
            ServicioCotizado servicio = ServicioCotizado.builder()
                    .descripcionServicio(s.getDescripcionServicio())
                    .cantidad(s.getCantidad())
                    .precioUnitario(s.getPrecioUnitario())
                    .build();
            cotizacion.addServicio(servicio);
        }
    }

    private CotizacionDTO toDTO(Cotizacion c) {
        return CotizacionDTO.builder()
                .idCotizacion(c.getIdCotizacion())
                .idCliente(c.getCliente().getIdCliente())
                .clienteRazonSocial(c.getCliente().getRazonSocial())
                .idUsuario(c.getUsuario().getIdUsuario())
                .usuarioNombre(c.getUsuario().getNombresUsuario() + " " + c.getUsuario().getApellidosUsuario())
                .idDireccionEntrega(c.getDireccionEntrega() != null ? c.getDireccionEntrega().getIdDireccion() : null)
                .codigoCotizacion(c.getCodigoCotizacion())
                .origen(c.getOrigen())
                .destino(c.getDestino())
                .tipoCarga(c.getTipoCarga())
                .pesoKg(c.getPesoKg())
                .estadoCotizacion(c.getEstadoCotizacion())
                .servicios(c.getServicios().stream().map(this::toServicioDTO).collect(Collectors.toList()))
                .total(c.calcularTotal())
                .build();
    }

    private ServicioCotizadoDTO toServicioDTO(ServicioCotizado s) {
        return ServicioCotizadoDTO.builder()
                .idServicio(s.getIdServicio())
                .idCotizacion(s.getCotizacion().getIdCotizacion())
                .descripcionServicio(s.getDescripcionServicio())
                .cantidad(s.getCantidad())
                .precioUnitario(s.getPrecioUnitario())
                .subtotal(s.calcularSubtotal())
                .build();
    }
}