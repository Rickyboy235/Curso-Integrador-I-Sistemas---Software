package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.LiquidacionTributosDTO;
import transflow.service.LiquidacionTributosService;

import java.util.List;

@RestController
@RequestMapping("/api/liquidaciones-tributos")
@RequiredArgsConstructor
public class LiquidacionTributosController {

    private final LiquidacionTributosService liquidacionTributosService;

    @GetMapping
    public ResponseEntity<List<LiquidacionTributosDTO>> listar() {
        return ResponseEntity.ok(liquidacionTributosService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionTributosDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(liquidacionTributosService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<LiquidacionTributosDTO> crear(@Valid @RequestBody LiquidacionTributosDTO dto) {
        LiquidacionTributosDTO creada = liquidacionTributosService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquidacionTributosDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody LiquidacionTributosDTO dto) {
        return ResponseEntity.ok(liquidacionTributosService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        liquidacionTributosService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}