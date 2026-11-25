package cl.tacticaldev.usuarios.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarError(
            IllegalArgumentException exception) {

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", exception.getMessage());

        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException exception) {

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Los datos enviados no son válidos");

        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErrorGeneral(
            Exception exception) {

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Ocurrió un error interno en el servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(respuesta);
    }
}