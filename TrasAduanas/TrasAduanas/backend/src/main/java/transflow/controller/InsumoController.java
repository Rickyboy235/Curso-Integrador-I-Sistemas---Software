package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.InsumoDTO;
import transflow.service.InsumoService;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
public class InsumoController {

    private final InsumoService insumoService;

    @GetMapping
    public ResponseEntity<List<InsumoDTO>> listar() {
        return ResponseEntity.ok(insumoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(insumoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<InsumoDTO> crear(@Valid @RequestBody InsumoDTO dto) {
        InsumoDTO creado = insumoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody InsumoDTO dto) {
        return ResponseEntity.ok(insumoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        insumoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}