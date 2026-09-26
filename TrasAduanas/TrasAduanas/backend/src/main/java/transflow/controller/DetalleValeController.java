package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.DetalleValeDTO;
import transflow.service.DetalleValeService;

import java.util.List;

@RestController
@RequestMapping("/api/vales-salida/{idVale}/detalles")
@RequiredArgsConstructor
public class DetalleValeController {

    private final DetalleValeService detalleValeService;

    @GetMapping
    public ResponseEntity<List<DetalleValeDTO>> listar(@PathVariable Integer idVale) {
        return ResponseEntity.ok(detalleValeService.listarPorVale(idVale));
    }

    @PostMapping
    public ResponseEntity<DetalleValeDTO> agregar(@PathVariable Integer idVale, @Valid @RequestBody DetalleValeDTO dto) {
        DetalleValeDTO creado = detalleValeService.agregar(idVale, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{idInsumo}")
    public ResponseEntity<DetalleValeDTO> actualizar(@PathVariable Integer idVale, @PathVariable Integer idInsumo, @Valid @RequestBody DetalleValeDTO dto) {
        return ResponseEntity.ok(detalleValeService.actualizar(idVale, idInsumo, dto));
    }

    @DeleteMapping("/{idInsumo}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idVale, @PathVariable Integer idInsumo) {
        detalleValeService.eliminar(idVale, idInsumo);
        return ResponseEntity.noContent().build();
    }
}