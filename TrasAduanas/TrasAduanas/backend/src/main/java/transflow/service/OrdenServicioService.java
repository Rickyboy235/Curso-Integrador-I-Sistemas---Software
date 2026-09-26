package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.OrdenServicioDTO;
import transflow.model.Conductor;
import transflow.model.Cotizacion;
import transflow.model.OrdenServicio;
import transflow.model.Vehiculo;
import transflow.repository.ConductorRepository;
import transflow.repository.CotizacionRepository;
import transflow.repository.OrdenServicioRepository;
import transflow.repository.VehiculoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenServicioService {

    private final OrdenServicioRepository ordenServicioRepository;
    private final CotizacionRepository cotizacionRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;

    @Transactional(readOnly = true)
    public List<OrdenServicioDTO> listarTodos() {
        return ordenServicioRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdenServicioDTO obtenerPorId(Integer id) {
        OrdenServicio orden = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada con id: " + id));
        return toDTO(orden);
    }

    @Transactional
    public OrdenServicioDTO crear(OrdenServicioDTO dto) {
        if (ordenServicioRepository.findByNumeroOrden(dto.getNumeroOrden()).isPresent()) {
            throw new RuntimeException("Ya existe una orden con el numero: " + dto.getNumeroOrden());
        }

        Cotizacion cotizacion = cotizacionRepository.findById(dto.getIdCotizacion())
                .orElseThrow(() -> new RuntimeException("Cotizacion no encontrada con id: " + dto.getIdCotizacion()));

        Vehiculo vehiculo = resolverVehiculo(dto.getIdVehiculo());
        Conductor conductor = resolverConductor(dto.getIdConductor());

        validarVehiculoDisponible(vehiculo, null);
        validarConductorDisponible(conductor, null);

        OrdenServicio orden = OrdenServicio.builder()
                .cotizacion(cotizacion)
                .vehiculo(vehiculo)
                .conductor(conductor)
                .numeroOrden(dto.getNumeroOrden())
                .fechaProgramada(dto.getFechaProgramada())
                .estadoServicio(dto.getEstadoServicio())
                .build();

        return toDTO(ordenServicioRepository.save(orden));
    }

    @Transactional
    public OrdenServicioDTO actualizar(Integer id, OrdenServicioDTO dto) {
        OrdenServicio orden = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada con id: " + id));

        Cotizacion cotizacion = cotizacionRepository.findById(dto.getIdCotizacion())
                .orElseThrow(() -> new RuntimeException("Cotizacion no encontrada con id: " + dto.getIdCotizacion()));

        Vehiculo vehiculo = resolverVehiculo(dto.getIdVehiculo());
        Conductor conductor = resolverConductor(dto.getIdConductor());

        validarVehiculoDisponible(vehiculo, id);
        validarConductorDisponible(conductor, id);

        orden.setCotizacion(cotizacion);
        orden.setVehiculo(vehiculo);
        orden.setConductor(conductor);
        orden.setNumeroOrden(dto.getNumeroOrden());
        orden.setFechaProgramada(dto.getFechaProgramada());
        orden.setEstadoServicio(dto.getEstadoServicio());

        return toDTO(ordenServicioRepository.save(orden));
    }

    private void validarVehiculoDisponible(Vehiculo vehiculo, Integer idOrdenActual) {
    if (vehiculo == null) return;

    if (!vehiculo.esAptoParaCircular()) {
        throw new RuntimeException(
                "El vehiculo con placa " + vehiculo.getPlacaTracto()
                        + " tiene el SOAT o CITV vencido y no puede ser asignado.");
    }

    boolean yaOcupado = ordenServicioRepository.findByVehiculo_IdVehiculo(vehiculo.getIdVehiculo()).stream()
            .anyMatch(o -> !o.getIdOrden().equals(idOrdenActual)
                    && !"finalizada".equalsIgnoreCase(o.getEstadoServicio()));

    if (yaOcupado) {
        throw new RuntimeException(
                "El vehiculo con placa " + vehiculo.getPlacaTracto()
                        + " ya esta asignado a otra orden de servicio activa.");
    }
}

    private void validarConductorDisponible(Conductor conductor, Integer idOrdenActual) {
    if (conductor == null) return;

    if (!conductor.licenciaVigente()) {
        throw new RuntimeException(
                "El conductor " + conductor.getNombres() + " " + conductor.getApellidos()
                        + " tiene la licencia vencida y no puede ser asignado.");
    }

    boolean yaOcupado = ordenServicioRepository.findByConductor_IdConductor(conductor.getIdConductor()).stream()
            .anyMatch(o -> !o.getIdOrden().equals(idOrdenActual)
                    && !"finalizada".equalsIgnoreCase(o.getEstadoServicio()));

    if (yaOcupado) {
        throw new RuntimeException(
                "El conductor " + conductor.getNombres() + " " + conductor.getApellidos()
                        + " ya esta asignado a otra orden de servicio activa.");
    }
}

    @Transactional
    public void eliminar(Integer id) {

        if (!ordenServicioRepository.existsById(id)) {
            throw new RuntimeException(
                "Orden de servicio no encontrada con id: " + id
            );
        }

        try {

            ordenServicioRepository.deleteById(id);
            ordenServicioRepository.flush();

        } catch (org.springframework.dao.DataIntegrityViolationException e) {

            throw new RuntimeException(
                    "No se puede eliminar la orden de servicio porque está asociada a otros registros."
            );
        }
    }

    private Vehiculo resolverVehiculo(Integer idVehiculo) {
        if (idVehiculo == null) return null;
        return vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con id: " + idVehiculo));
    }

    private Conductor resolverConductor(Integer idConductor) {
        if (idConductor == null) return null;
        return conductorRepository.findById(idConductor)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado con id: " + idConductor));
    }

    private OrdenServicioDTO toDTO(OrdenServicio o) {
        return OrdenServicioDTO.builder()
                .idOrden(o.getIdOrden())
                .idCotizacion(o.getCotizacion().getIdCotizacion())
                .idVehiculo(o.getVehiculo() != null ? o.getVehiculo().getIdVehiculo() : null)
                .idConductor(o.getConductor() != null ? o.getConductor().getIdConductor() : null)
                .numeroOrden(o.getNumeroOrden())
                .fechaProgramada(o.getFechaProgramada())
                .estadoServicio(o.getEstadoServicio())
                .build();
    }
}