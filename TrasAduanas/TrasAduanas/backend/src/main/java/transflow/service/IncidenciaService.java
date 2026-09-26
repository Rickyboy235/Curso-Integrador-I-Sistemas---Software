package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.IncidenciaDTO;
import transflow.model.Incidencia;
import transflow.model.OrdenServicio;
import transflow.repository.IncidenciaRepository;
import transflow.repository.OrdenServicioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public List<IncidenciaDTO> listarTodos() {
        return incidenciaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidenciaDTO obtenerPorId(Integer id) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada con id: " + id));
        return toDTO(incidencia);
    }

    @Transactional
    public IncidenciaDTO crear(IncidenciaDTO dto) {
        OrdenServicio orden = ordenServicioRepository.findById(dto.getIdOrdenServicio())
                .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada con id: " + dto.getIdOrdenServicio()));

        Incidencia incidencia = Incidencia.builder()
                .ordenServicio(orden)
                .tipoIncidencia(dto.getTipoIncidencia())
                .descripcion(dto.getDescripcion())
                .fechaReporte(dto.getFechaReporte())
                .build();
        incidencia.registrar();

        return toDTO(incidenciaRepository.save(incidencia));
    }

    @Transactional
    public IncidenciaDTO actualizar(Integer id, IncidenciaDTO dto) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada con id: " + id));

        OrdenServicio orden = ordenServicioRepository.findById(dto.getIdOrdenServicio())
                .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada con id: " + dto.getIdOrdenServicio()));

        incidencia.setOrdenServicio(orden);
        incidencia.setTipoIncidencia(dto.getTipoIncidencia());
        incidencia.setDescripcion(dto.getDescripcion());
        if (dto.getFechaReporte() != null) {
            incidencia.setFechaReporte(dto.getFechaReporte());
        }

        return toDTO(incidenciaRepository.save(incidencia));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!incidenciaRepository.existsById(id)) {
            throw new RuntimeException("Incidencia no encontrada con id: " + id);
        }
        incidenciaRepository.deleteById(id);
    }

    private IncidenciaDTO toDTO(Incidencia i) {
        return IncidenciaDTO.builder()
                .idIncidencia(i.getIdIncidencia())
                .idOrdenServicio(i.getOrdenServicio().getIdOrden())
                .tipoIncidencia(i.getTipoIncidencia())
                .descripcion(i.getDescripcion())
                .fechaReporte(i.getFechaReporte())
                .build();
    }
}