package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.ServicioCotizadoDTO;
import transflow.model.Cotizacion;
import transflow.model.ServicioCotizado;
import transflow.repository.CotizacionRepository;
import transflow.repository.ServicioCotizadoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioCotizadoService {

    private final ServicioCotizadoRepository servicioCotizadoRepository;
    private final CotizacionRepository cotizacionRepository;

    @Transactional(readOnly = true)
    public List<ServicioCotizadoDTO> listarPorCotizacion(Integer idCotizacion) {
        return servicioCotizadoRepository.findByCotizacion_IdCotizacion(idCotizacion).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServicioCotizadoDTO agregar(Integer idCotizacion, ServicioCotizadoDTO dto) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotizacion no encontrada con id: " + idCotizacion));

        ServicioCotizado servicio = ServicioCotizado.builder()
                .descripcionServicio(dto.getDescripcionServicio())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .build();

        cotizacion.addServicio(servicio);
        cotizacionRepository.save(cotizacion);

        return toDTO(servicio);
    }

    @Transactional
    public ServicioCotizadoDTO actualizar(Integer idCotizacion, Integer idServicio, ServicioCotizadoDTO dto) {
        ServicioCotizado servicio = servicioCotizadoRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + idServicio));

        if (!servicio.getCotizacion().getIdCotizacion().equals(idCotizacion)) {
            throw new RuntimeException("El servicio no pertenece a la cotizacion: " + idCotizacion);
        }

        servicio.setDescripcionServicio(dto.getDescripcionServicio());
        servicio.setCantidad(dto.getCantidad());
        servicio.setPrecioUnitario(dto.getPrecioUnitario());

        return toDTO(servicioCotizadoRepository.save(servicio));
    }

    @Transactional
    public void eliminar(Integer idCotizacion, Integer idServicio) {
        ServicioCotizado servicio = servicioCotizadoRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + idServicio));

        if (!servicio.getCotizacion().getIdCotizacion().equals(idCotizacion)) {
            throw new RuntimeException("El servicio no pertenece a la cotizacion: " + idCotizacion);
        }

        servicioCotizadoRepository.deleteById(idServicio);
    }

    private ServicioCotizadoDTO toDTO(ServicioCotizado s) {
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