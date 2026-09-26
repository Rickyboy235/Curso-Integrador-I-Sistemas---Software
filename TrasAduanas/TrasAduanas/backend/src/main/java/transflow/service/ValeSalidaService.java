package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.ValeSalidaDTO;
import transflow.model.OrdenServicio;
import transflow.model.ValeSalida;
import transflow.repository.OrdenServicioRepository;
import transflow.repository.ValeSalidaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValeSalidaService {

    private final ValeSalidaRepository valeSalidaRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public List<ValeSalidaDTO> listarTodos() {
        return valeSalidaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ValeSalidaDTO obtenerPorId(Integer id) {
        ValeSalida vale = valeSalidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vale de salida no encontrado con id: " + id));
        return toDTO(vale);
    }

    @Transactional
    public ValeSalidaDTO crear(ValeSalidaDTO dto) {
        if (valeSalidaRepository.findByNumeroVale(dto.getNumeroVale()).isPresent()) {
            throw new RuntimeException("Ya existe un vale con el numero: " + dto.getNumeroVale());
        }

        OrdenServicio orden = ordenServicioRepository.findById(dto.getIdOrdenServicio())
                .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada con id: " + dto.getIdOrdenServicio()));

        ValeSalida vale = ValeSalida.builder()
                .ordenServicio(orden)
                .numeroVale(dto.getNumeroVale())
                .fechaEmision(dto.getFechaEmision())
                .build();
        vale.emitir();

        return toDTO(valeSalidaRepository.save(vale));
    }

    @Transactional
    public ValeSalidaDTO actualizar(Integer id, ValeSalidaDTO dto) {
        ValeSalida vale = valeSalidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vale de salida no encontrado con id: " + id));

        vale.setNumeroVale(dto.getNumeroVale());
        if (dto.getFechaEmision() != null) {
            vale.setFechaEmision(dto.getFechaEmision());
        }

        return toDTO(valeSalidaRepository.save(vale));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!valeSalidaRepository.existsById(id)) {
            throw new RuntimeException("Vale de salida no encontrado con id: " + id);
        }
        valeSalidaRepository.deleteById(id);
    }

    private ValeSalidaDTO toDTO(ValeSalida v) {
        return ValeSalidaDTO.builder()
                .idVale(v.getIdVale())
                .idOrdenServicio(v.getOrdenServicio().getIdOrden())
                .numeroVale(v.getNumeroVale())
                .fechaEmision(v.getFechaEmision())
                .build();
    }
}