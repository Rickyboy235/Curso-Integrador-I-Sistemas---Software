package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.ConductorDTO;
import transflow.service.ConductorService;

import java.util.List;

@RestController
@RequestMapping("/api/conductores")
@RequiredArgsConstructor
public class ConductorController {

    private final ConductorService conductorService;

    @GetMapping
    public ResponseEntity<List<ConductorDTO>> listar() {
        return ResponseEntity.ok(conductorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConductorDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(conductorService.obtenerPorId(id));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ConductorDTO>> listarDisponibles() {
        return ResponseEntity.ok(conductorService.listarDisponibles());
    }

    @PostMapping
    public ResponseEntity<ConductorDTO> crear(@Valid @RequestBody ConductorDTO dto) {
        ConductorDTO creado = conductorService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConductorDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ConductorDTO dto) {
        return ResponseEntity.ok(conductorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        conductorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}