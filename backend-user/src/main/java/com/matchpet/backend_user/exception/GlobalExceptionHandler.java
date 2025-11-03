package com.matchpet.backend_user.exception;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * ¡FIX PARA ERROR 2!
 * Esta clase "atrapa" excepciones específicas que ocurren en CUALQUIER
 * controlador y las convierte en una respuesta JSON limpia (como 401 o 400),
 * en lugar de dejar que la app crashee con un 500.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Atrapa cualquier error relacionado con JWT (token malformado, expirado, etc.)
     * que pueda ocurrir en el endpoint /refresh.
     */
    @ExceptionHandler({ JwtException.class, MalformedJwtException.class })
    public ResponseEntity<Map<String, String>> handleJwtException(Exception ex) {
        // Devuelve un 401 Unauthorized (No Autorizado) limpio
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Token inválido o expirado", "detalle", ex.getMessage()));
    }

    /**
     * Atrapa errores de lógica de negocio (como "email ya existe")
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(RuntimeException ex) {
        // Devuelve un 400 Bad Request (Petición Inválida)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}
