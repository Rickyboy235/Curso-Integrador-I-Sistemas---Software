package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.DetalleValeDTO;
import transflow.model.DetalleVale;
import transflow.model.DetalleValeId;
import transflow.model.Insumo;
import transflow.model.ValeSalida;
import transflow.repository.DetalleValeRepository;
import transflow.repository.InsumoRepository;
import transflow.repository.ValeSalidaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleValeService {

    private final DetalleValeRepository detalleValeRepository;
    private final ValeSalidaRepository valeSalidaRepository;
    private final InsumoRepository insumoRepository;

    @Transactional(readOnly = true)
    public List<DetalleValeDTO> listarPorVale(Integer idVale) {
        return detalleValeRepository.findByValeSalida_IdVale(idVale).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetalleValeDTO agregar(Integer idVale, DetalleValeDTO dto) {
        ValeSalida vale = valeSalidaRepository.findById(idVale)
                .orElseThrow(() -> new RuntimeException("Vale de salida no encontrado con id: " + idVale));

        Insumo insumo = insumoRepository.findById(dto.getIdInsumo())
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado con id: " + dto.getIdInsumo()));

        if (insumo.getStockActual().compareTo(dto.getCantidad()) < 0) {
            throw new RuntimeException("Stock insuficiente para el insumo " + insumo.getCodigo()
                    + " (disponible: " + insumo.getStockActual() + ")");
        }

        DetalleVale detalle = DetalleVale.builder()
                .insumo(insumo)
                .cantidad(dto.getCantidad())
                .build();

        vale.addDetalle(detalle);
        insumo.descontarStock(dto.getCantidad());

        valeSalidaRepository.save(vale);
        insumoRepository.save(insumo);

        return toDTO(detalle);
    }

    @Transactional
    public DetalleValeDTO actualizar(Integer idVale, Integer idInsumo, DetalleValeDTO dto) {
        DetalleValeId id = new DetalleValeId(idVale, idInsumo);
        DetalleVale detalle = detalleValeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado para vale " + idVale + " e insumo " + idInsumo));

        Insumo insumo = detalle.getInsumo();
        BigDecimal diferencia = dto.getCantidad().subtract(detalle.getCantidad());

        if (diferencia.compareTo(BigDecimal.ZERO) > 0 && insumo.getStockActual().compareTo(diferencia) < 0) {
            throw new RuntimeException("Stock insuficiente para el insumo " + insumo.getCodigo()
                    + " (disponible: " + insumo.getStockActual() + ")");
        }

        insumo.descontarStock(diferencia);
        insumoRepository.save(insumo);

        detalle.setCantidad(dto.getCantidad());

        return toDTO(detalleValeRepository.save(detalle));
    }

    @Transactional
    public void eliminar(Integer idVale, Integer idInsumo) {
        DetalleValeId id = new DetalleValeId(idVale, idInsumo);
        DetalleVale detalle = detalleValeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado para vale " + idVale + " e insumo " + idInsumo));

        Insumo insumo = detalle.getInsumo();
        insumo.descontarStock(detalle.getCantidad().negate());
        insumoRepository.save(insumo);

        detalleValeRepository.deleteById(id);
    }

    private DetalleValeDTO toDTO(DetalleVale d) {
        return DetalleValeDTO.builder()
                .idVale(d.getValeSalida().getIdVale())
                .idInsumo(d.getInsumo().getIdInsumo())
                .cantidad(d.getCantidad())
                .build();
    }
}