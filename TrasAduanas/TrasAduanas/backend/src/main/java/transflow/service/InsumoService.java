package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.InsumoDTO;
import transflow.model.Insumo;
import transflow.repository.InsumoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsumoService {

    private final InsumoRepository insumoRepository;

    @Transactional(readOnly = true)
    public List<InsumoDTO> listarTodos() {
        return insumoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InsumoDTO obtenerPorId(Integer id) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado con id: " + id));
        return toDTO(insumo);
    }

    @Transactional
    public InsumoDTO crear(InsumoDTO dto) {
        if (insumoRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new RuntimeException("Ya existe un insumo con el codigo: " + dto.getCodigo());
        }

        Insumo insumo = Insumo.builder()
                .codigo(dto.getCodigo())
                .descripcion(dto.getDescripcion())
                .stockActual(dto.getStockActual())
                .stockMinimo(dto.getStockMinimo())
                .build();

        return toDTO(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoDTO actualizar(Integer id, InsumoDTO dto) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado con id: " + id));

        insumo.setCodigo(dto.getCodigo());
        insumo.setDescripcion(dto.getDescripcion());
        insumo.setStockActual(dto.getStockActual());
        insumo.setStockMinimo(dto.getStockMinimo());

        return toDTO(insumoRepository.save(insumo));
    }

    @Transactional
    public void eliminar(Integer id) {

        if (!insumoRepository.existsById(id)) {
            throw new RuntimeException("Insumo no encontrado con id: " + id);
        }

        try {

            insumoRepository.deleteById(id);
            insumoRepository.flush();

        } catch (org.springframework.dao.DataIntegrityViolationException e) {

            throw new RuntimeException(
                    "No se puede eliminar el insumo porque está siendo utilizado en un vale de salida."
            );
        }
    }

    private InsumoDTO toDTO(Insumo i) {
        return InsumoDTO.builder()
                .idInsumo(i.getIdInsumo())
                .codigo(i.getCodigo())
                .descripcion(i.getDescripcion())
                .stockActual(i.getStockActual())
                .stockMinimo(i.getStockMinimo())
                .build();
    }
}