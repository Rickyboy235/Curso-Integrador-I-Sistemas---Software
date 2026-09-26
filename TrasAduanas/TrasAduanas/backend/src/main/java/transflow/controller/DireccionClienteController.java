package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.DireccionClienteDTO;
import transflow.service.DireccionClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/{idCliente}/direcciones")
@RequiredArgsConstructor
public class DireccionClienteController {

    private final DireccionClienteService direccionClienteService;

    @GetMapping
    public ResponseEntity<List<DireccionClienteDTO>> listar(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(direccionClienteService.listarPorCliente(idCliente));
    }

    @PostMapping
    public ResponseEntity<DireccionClienteDTO> agregar(@PathVariable Integer idCliente, @Valid @RequestBody DireccionClienteDTO dto) {
        DireccionClienteDTO creada = direccionClienteService.agregar(idCliente, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<DireccionClienteDTO> actualizar(@PathVariable Integer idCliente, @PathVariable Integer idDireccion, @Valid @RequestBody DireccionClienteDTO dto) {
        return ResponseEntity.ok(direccionClienteService.actualizar(idCliente, idDireccion, dto));
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idCliente, @PathVariable Integer idDireccion) {
        direccionClienteService.eliminar(idCliente, idDireccion);
        return ResponseEntity.noContent().build();
    }
}