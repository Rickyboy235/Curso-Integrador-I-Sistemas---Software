package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.LiquidacionTributosDTO;
import transflow.model.ExpedienteDAM;
import transflow.model.LiquidacionTributos;
import transflow.repository.ExpedienteDAMRepository;
import transflow.repository.LiquidacionTributosRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquidacionTributosService {

    private final LiquidacionTributosRepository liquidacionTributosRepository;
    private final ExpedienteDAMRepository expedienteDAMRepository;

    @Transactional(readOnly = true)
    public List<LiquidacionTributosDTO> listarTodos() {
        return liquidacionTributosRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LiquidacionTributosDTO obtenerPorId(Integer id) {
        LiquidacionTributos liquidacion = liquidacionTributosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liquidacion no encontrada con id: " + id));
        return toDTO(liquidacion);
    }

    @Transactional
    public LiquidacionTributosDTO crear(LiquidacionTributosDTO dto) {
        if (liquidacionTributosRepository.findByExpedienteDam_IdExpediente(dto.getIdExpedienteDam()).isPresent()) {
            throw new RuntimeException("Ya existe una liquidacion para el expediente: " + dto.getIdExpedienteDam());
        }

        ExpedienteDAM expediente = expedienteDAMRepository.findById(dto.getIdExpedienteDam())
                .orElseThrow(() -> new RuntimeException("Expediente DAM no encontrado con id: " + dto.getIdExpedienteDam()));

        LiquidacionTributos liquidacion = LiquidacionTributos.builder()
                .expedienteDam(expediente)
                .adValorem(dto.getAdValorem())
                .igv(dto.getIgv())
                .ipm(dto.getIpm())
                .estadoPago(dto.getEstadoPago())
                .build();

        return toDTO(liquidacionTributosRepository.save(liquidacion));
    }

    @Transactional
    public LiquidacionTributosDTO actualizar(Integer id, LiquidacionTributosDTO dto) {
        LiquidacionTributos liquidacion = liquidacionTributosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liquidacion no encontrada con id: " + id));

        liquidacion.setAdValorem(dto.getAdValorem());
        liquidacion.setIgv(dto.getIgv());
        liquidacion.setIpm(dto.getIpm());
        liquidacion.setEstadoPago(dto.getEstadoPago());

        return toDTO(liquidacionTributosRepository.save(liquidacion));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!liquidacionTributosRepository.existsById(id)) {
            throw new RuntimeException("Liquidacion no encontrada con id: " + id);
        }
        liquidacionTributosRepository.deleteById(id);
    }

    private LiquidacionTributosDTO toDTO(LiquidacionTributos l) {
        return LiquidacionTributosDTO.builder()
                .idLiquidacion(l.getIdLiquidacion())
                .idExpedienteDam(l.getExpedienteDam().getIdExpediente())
                .adValorem(l.getAdValorem())
                .igv(l.getIgv())
                .ipm(l.getIpm())
                .estadoPago(l.getEstadoPago())
                .totalTributos(l.calcularTotalTributos())
                .build();
    }
}