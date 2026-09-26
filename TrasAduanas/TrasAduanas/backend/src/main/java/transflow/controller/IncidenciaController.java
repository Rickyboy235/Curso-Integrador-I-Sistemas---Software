package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.IncidenciaDTO;
import transflow.service.IncidenciaService;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @GetMapping
    public ResponseEntity<List<IncidenciaDTO>> listar() {
        return ResponseEntity.ok(incidenciaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(incidenciaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<IncidenciaDTO> crear(@Valid @RequestBody IncidenciaDTO dto) {
        IncidenciaDTO creada = incidenciaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody IncidenciaDTO dto) {
        return ResponseEntity.ok(incidenciaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        incidenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}