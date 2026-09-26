package transflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transflow.dto.ServicioCotizadoDTO;
import transflow.service.ServicioCotizadoService;

import java.util.List;

@RestController
@RequestMapping("/api/cotizaciones/{idCotizacion}/servicios")
@RequiredArgsConstructor
public class ServicioCotizadoController {

    private final ServicioCotizadoService servicioCotizadoService;

    @GetMapping
    public ResponseEntity<List<ServicioCotizadoDTO>> listar(@PathVariable Integer idCotizacion) {
        return ResponseEntity.ok(servicioCotizadoService.listarPorCotizacion(idCotizacion));
    }

    @PostMapping
    public ResponseEntity<ServicioCotizadoDTO> agregar(@PathVariable Integer idCotizacion, @Valid @RequestBody ServicioCotizadoDTO dto) {
        ServicioCotizadoDTO creado = servicioCotizadoService.agregar(idCotizacion, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{idServicio}")
    public ResponseEntity<ServicioCotizadoDTO> actualizar(@PathVariable Integer idCotizacion, @PathVariable Integer idServicio, @Valid @RequestBody ServicioCotizadoDTO dto) {
        return ResponseEntity.ok(servicioCotizadoService.actualizar(idCotizacion, idServicio, dto));
    }

    @DeleteMapping("/{idServicio}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idCotizacion, @PathVariable Integer idServicio) {
        servicioCotizadoService.eliminar(idCotizacion, idServicio);
        return ResponseEntity.noContent().build();
    }
}