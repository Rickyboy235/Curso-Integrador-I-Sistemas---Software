package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.ValeSalidaDTO;
import transflow.service.ValeSalidaService;

import java.util.List;

@RestController
@RequestMapping("/api/vales-salida")
@RequiredArgsConstructor
public class ValeSalidaController {

    private final ValeSalidaService valeSalidaService;

    @GetMapping
    public ResponseEntity<List<ValeSalidaDTO>> listar() {
        return ResponseEntity.ok(valeSalidaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValeSalidaDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(valeSalidaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ValeSalidaDTO> crear(@Valid @RequestBody ValeSalidaDTO dto) {
        ValeSalidaDTO creado = valeSalidaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ValeSalidaDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ValeSalidaDTO dto) {
        return ResponseEntity.ok(valeSalidaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        valeSalidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}