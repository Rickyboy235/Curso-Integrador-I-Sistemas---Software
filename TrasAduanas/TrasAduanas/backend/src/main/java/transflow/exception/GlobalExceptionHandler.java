package transflow.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> cuerpo = cuerpoBase(HttpStatus.BAD_REQUEST, "Datos invalidos");
        cuerpo.put("detalles", errores);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        String mensaje = "El parametro '" + ex.getName() + "' no tiene un formato valido";
        return ResponseEntity.badRequest().body(cuerpoBase(HttpStatus.BAD_REQUEST, mensaje));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(DataIntegrityViolationException ex) {
        String mensaje = "El dato no cumple una restriccion de la base de datos "
                + "(duplicado, campo obligatorio faltante o referencia invalida).";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpoBase(HttpStatus.CONFLICT, mensaje));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntime(RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Ocurrio un error inesperado";
        String mensajeMinuscula = mensaje.toLowerCase();

        HttpStatus status;
        if (mensajeMinuscula.contains("no encontrad")) {
            status = HttpStatus.NOT_FOUND;
        } else if (mensajeMinuscula.contains("ya existe")) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(cuerpoBase(status, mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGenerico(Exception ex) {
        String mensaje = "Ocurrio un error inesperado en el servidor.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(cuerpoBase(HttpStatus.INTERNAL_SERVER_ERROR, mensaje));
    }

    private Map<String, Object> cuerpoBase(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("error", mensaje);
        return cuerpo;
    }
}