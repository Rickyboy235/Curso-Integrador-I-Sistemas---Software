package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.CotizacionDTO;
import transflow.service.CotizacionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;

    @GetMapping
    public ResponseEntity<List<CotizacionDTO>> listar() {
        return ResponseEntity.ok(cotizacionService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(cotizacionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CotizacionDTO> crear(@Valid @RequestBody CotizacionDTO dto) {
        CotizacionDTO creada = cotizacionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CotizacionDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody CotizacionDTO dto) {
        return ResponseEntity.ok(cotizacionService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CotizacionDTO> cambiarEstado(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estadoCotizacion");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cotizacionService.cambiarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        cotizacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}