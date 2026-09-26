package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.ExpedienteDAMDTO;
import transflow.service.ExpedienteDAMService;

import java.util.List;

@RestController
@RequestMapping("/api/expedientes-dam")
@RequiredArgsConstructor
public class ExpedienteDAMController {

    private final ExpedienteDAMService expedienteDAMService;

    @GetMapping
    public ResponseEntity<List<ExpedienteDAMDTO>> listar() {
        return ResponseEntity.ok(expedienteDAMService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteDAMDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(expedienteDAMService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ExpedienteDAMDTO> crear(@Valid @RequestBody ExpedienteDAMDTO dto) {
        ExpedienteDAMDTO creado = expedienteDAMService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpedienteDAMDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ExpedienteDAMDTO dto) {
        return ResponseEntity.ok(expedienteDAMService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        expedienteDAMService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}