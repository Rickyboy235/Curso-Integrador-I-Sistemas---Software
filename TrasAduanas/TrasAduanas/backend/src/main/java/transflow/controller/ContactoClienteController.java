package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.ContactoClienteDTO;
import transflow.service.ContactoClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/{idCliente}/contactos")
@RequiredArgsConstructor
public class ContactoClienteController {

    private final ContactoClienteService contactoClienteService;

    @GetMapping
    public ResponseEntity<List<ContactoClienteDTO>> listar(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(contactoClienteService.listarPorCliente(idCliente));
    }

    @PostMapping
    public ResponseEntity<ContactoClienteDTO> agregar(@PathVariable Integer idCliente, @Valid @RequestBody ContactoClienteDTO dto) {
        ContactoClienteDTO creado = contactoClienteService.agregar(idCliente, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{idContacto}")
    public ResponseEntity<ContactoClienteDTO> actualizar(@PathVariable Integer idCliente, @PathVariable Integer idContacto, @Valid @RequestBody ContactoClienteDTO dto) {
        return ResponseEntity.ok(contactoClienteService.actualizar(idCliente, idContacto, dto));
    }

    @DeleteMapping("/{idContacto}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idCliente, @PathVariable Integer idContacto) {
        contactoClienteService.eliminar(idCliente, idContacto);
        return ResponseEntity.noContent().build();
    }
}