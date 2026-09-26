package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.ConductorDTO;
import transflow.model.Conductor;
import transflow.repository.ConductorRepository;
import transflow.repository.OrdenServicioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConductorService {

    private final ConductorRepository conductorRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public List<ConductorDTO> listarTodos() {
        return conductorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConductorDTO obtenerPorId(Integer id) {
        Conductor conductor = conductorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado con id: " + id));
        return toDTO(conductor);
    }

    @Transactional
    public ConductorDTO crear(ConductorDTO dto) {
        if (conductorRepository.findByNumDoc(dto.getNumDoc()).isPresent()) {
            throw new RuntimeException("Ya existe un conductor con el documento: " + dto.getNumDoc());
        }
        if (conductorRepository.findByLicencia(dto.getLicencia()).isPresent()) {
            throw new RuntimeException("Ya existe un conductor con la licencia: " + dto.getLicencia());
        }

        Conductor conductor = Conductor.builder()
                .numDoc(dto.getNumDoc())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .licencia(dto.getLicencia())
                .categoria(dto.getCategoria())
                .vencimientoLicencia(dto.getVencimientoLicencia())
                .build();

        return toDTO(conductorRepository.save(conductor));
    }

    @Transactional
    public ConductorDTO actualizar(Integer id, ConductorDTO dto) {
        Conductor conductor = conductorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado con id: " + id));

        conductor.setNumDoc(dto.getNumDoc());
        conductor.setNombres(dto.getNombres());
        conductor.setApellidos(dto.getApellidos());
        conductor.setLicencia(dto.getLicencia());
        conductor.setCategoria(dto.getCategoria());
        conductor.setVencimientoLicencia(dto.getVencimientoLicencia());

        return toDTO(conductorRepository.save(conductor));
    }

    @Transactional
    public void eliminar(Integer id) {

        if (!conductorRepository.existsById(id)) {
            throw new RuntimeException(
                    "Conductor no encontrado con id: " + id
            );
        }

        if (!ordenServicioRepository
                .findByConductor_IdConductor(id)
                .isEmpty()) {

            throw new RuntimeException(
                    "No se puede eliminar el conductor porque está asociado a una o más órdenes de servicio."
            );
        }

        conductorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ConductorDTO> listarDisponibles() {
    return conductorRepository.findAll().stream()
            .filter(Conductor::licenciaVigente)
            .filter(c -> !estaEnOrdenActiva(c.getIdConductor()))
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    private boolean estaEnOrdenActiva(Integer idConductor) {
    return ordenServicioRepository.findByConductor_IdConductor(idConductor).stream()
            .anyMatch(o -> !"finalizada".equalsIgnoreCase(o.getEstadoServicio()));
    }

    private ConductorDTO toDTO(Conductor c) {
        return ConductorDTO.builder()
                .idConductor(c.getIdConductor())
                .numDoc(c.getNumDoc())
                .nombres(c.getNombres())
                .apellidos(c.getApellidos())
                .licencia(c.getLicencia())
                .categoria(c.getCategoria())
                .vencimientoLicencia(c.getVencimientoLicencia())
                .build();
    }
}