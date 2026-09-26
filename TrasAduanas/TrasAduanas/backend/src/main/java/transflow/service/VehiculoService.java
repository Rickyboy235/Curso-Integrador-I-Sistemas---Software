package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.VehiculoDTO;
import transflow.model.Vehiculo;
import transflow.repository.VehiculoRepository;
import transflow.repository.OrdenServicioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public List<VehiculoDTO> listarTodos() {
        return vehiculoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehiculoDTO obtenerPorId(Integer id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con id: " + id));
        return toDTO(vehiculo);
    }

    @Transactional
    public VehiculoDTO crear(VehiculoDTO dto) {
        if (vehiculoRepository.findByPlacaTracto(dto.getPlacaTracto()).isPresent()) {
            throw new RuntimeException("Ya existe un vehiculo con la placa: " + dto.getPlacaTracto());
        }

        Vehiculo vehiculo = Vehiculo.builder()
                .placaTracto(dto.getPlacaTracto())
                .placaCarreta(dto.getPlacaCarreta())
                .soatVencimiento(dto.getSoatVencimiento())
                .citvVencimiento(dto.getCitvVencimiento())
                .build();

        return toDTO(vehiculoRepository.save(vehiculo));
    }

    @Transactional
    public VehiculoDTO actualizar(Integer id, VehiculoDTO dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con id: " + id));

        vehiculo.setPlacaTracto(dto.getPlacaTracto());
        vehiculo.setPlacaCarreta(dto.getPlacaCarreta());
        vehiculo.setSoatVencimiento(dto.getSoatVencimiento());
        vehiculo.setCitvVencimiento(dto.getCitvVencimiento());

        return toDTO(vehiculoRepository.save(vehiculo));
    }

    @Transactional
    public void eliminar(Integer id) {

        if (!vehiculoRepository.existsById(id)) {
            throw new RuntimeException(
                    "Vehiculo no encontrado con id: " + id
            );
        }

        if (!ordenServicioRepository
                .findByVehiculo_IdVehiculo(id)
                .isEmpty()) {

            throw new RuntimeException(
                    "No se puede eliminar el vehículo porque está asociado a una o más órdenes de servicio."
            );
        }

        vehiculoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<VehiculoDTO> listarDisponibles() {
    return vehiculoRepository.findAll().stream()
            .filter(Vehiculo::esAptoParaCircular)
            .filter(v -> !estaEnOrdenActiva(v.getIdVehiculo()))
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    private boolean estaEnOrdenActiva(Integer idVehiculo) {
    return ordenServicioRepository.findByVehiculo_IdVehiculo(idVehiculo).stream()
            .anyMatch(o -> !"finalizada".equalsIgnoreCase(o.getEstadoServicio()));
    }

    private VehiculoDTO toDTO(Vehiculo v) {
        return VehiculoDTO.builder()
                .idVehiculo(v.getIdVehiculo())
                .placaTracto(v.getPlacaTracto())
                .placaCarreta(v.getPlacaCarreta())
                .soatVencimiento(v.getSoatVencimiento())
                .citvVencimiento(v.getCitvVencimiento())
                .build();
    }
}