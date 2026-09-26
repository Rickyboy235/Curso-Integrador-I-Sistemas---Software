package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.OrdenServicioDTO;
import transflow.service.OrdenServicioService;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-servicio")
@RequiredArgsConstructor
public class OrdenServicioController {

    private final OrdenServicioService ordenServicioService;

    @GetMapping
    public ResponseEntity<List<OrdenServicioDTO>> listar() {
        return ResponseEntity.ok(ordenServicioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenServicioDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenServicioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrdenServicioDTO> crear(@Valid @RequestBody OrdenServicioDTO dto) {
        OrdenServicioDTO creada = ordenServicioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenServicioDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody OrdenServicioDTO dto) {
        return ResponseEntity.ok(ordenServicioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        ordenServicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}