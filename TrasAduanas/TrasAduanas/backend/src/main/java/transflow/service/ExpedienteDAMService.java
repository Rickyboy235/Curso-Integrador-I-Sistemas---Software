package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.ExpedienteDAMDTO;
import transflow.model.ExpedienteDAM;
import transflow.model.OrdenServicio;
import transflow.repository.ExpedienteDAMRepository;
import transflow.repository.OrdenServicioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpedienteDAMService {

    private final ExpedienteDAMRepository expedienteDAMRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public List<ExpedienteDAMDTO> listarTodos() {
        return expedienteDAMRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpedienteDAMDTO obtenerPorId(Integer id) {
        ExpedienteDAM expediente = expedienteDAMRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expediente DAM no encontrado con id: " + id));
        return toDTO(expediente);
    }

    @Transactional
    public ExpedienteDAMDTO crear(ExpedienteDAMDTO dto) {
        OrdenServicio orden = ordenServicioRepository.findById(dto.getIdOrdenServicio())
                .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada con id: " + dto.getIdOrdenServicio()));

        ExpedienteDAM expediente = ExpedienteDAM.builder()
                .ordenServicio(orden)
                .numeroDam(dto.getNumeroDam())
                .regimen(dto.getRegimen())
                .canalControl(dto.getCanalControl())
                .estadoTramite(dto.getEstadoTramite())
                .build();

        return toDTO(expedienteDAMRepository.save(expediente));
    }

    @Transactional
    public ExpedienteDAMDTO actualizar(Integer id, ExpedienteDAMDTO dto) {
        ExpedienteDAM expediente = expedienteDAMRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expediente DAM no encontrado con id: " + id));

        OrdenServicio orden = ordenServicioRepository.findById(dto.getIdOrdenServicio())
                .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada con id: " + dto.getIdOrdenServicio()));

        expediente.setOrdenServicio(orden);
        expediente.setNumeroDam(dto.getNumeroDam());
        expediente.setRegimen(dto.getRegimen());
        expediente.setCanalControl(dto.getCanalControl());
        expediente.setEstadoTramite(dto.getEstadoTramite());

        return toDTO(expedienteDAMRepository.save(expediente));
    }

    @Transactional
    public void eliminar(Integer id) {

        if (!expedienteDAMRepository.existsById(id)) {
            throw new RuntimeException(
                "Expediente DAM no encontrado con id: " + id
            );
        }

        try {

            expedienteDAMRepository.deleteById(id);
            expedienteDAMRepository.flush();

        } catch (org.springframework.dao.DataIntegrityViolationException e) {

            throw new RuntimeException(
                    "No se puede eliminar el expediente porque está asociado a otros registros."
            );
        }
    }

    private ExpedienteDAMDTO toDTO(ExpedienteDAM e) {
        return ExpedienteDAMDTO.builder()
                .idExpediente(e.getIdExpediente())
                .idOrdenServicio(e.getOrdenServicio().getIdOrden())
                .numeroDam(e.getNumeroDam())
                .regimen(e.getRegimen())
                .canalControl(e.getCanalControl())
                .estadoTramite(e.getEstadoTramite())
                .build();
    }
}