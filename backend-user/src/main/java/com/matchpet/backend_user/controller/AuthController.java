package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.auth.*;
import com.matchpet.backend_user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Autenticación", description = "Endpoints para Registro y Login")
public class AuthController {

    private final AuthService authService;

    // --- ¡El endpoint POST /register SE HA QUITADO DE AQUÍ! ---
    // --- Ahora vive en AdoptanteController.java ---

    @Operation(summary = "Inicia sesión (Login)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Renueva un token de acceso (Refresh Token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de acceso renovado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Refresh Token inválido o expirado")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(
            summary = "Obtener URL para Login con Google (Solo Documentación)",
            description = "¡ESTE ENDPOINT NO SE LLAMA DIRECTAMENTE! \n\n" +
                    "Es solo para documentación. \n\n" +
                    "Para iniciar el login con Google, el frontend debe **redirigir** al usuario (con un link `<a>` o un `window.location.href`) " +
                    "a la URL que se devuelve en la respuesta: `/oauth2/authorization/google`"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devuelve la URL a la que el frontend debe redirigir al usuario")
    })
    @GetMapping("/google-login-url")
    public ResponseEntity<Map<String, String>> getGoogleLoginUrl() {
        return ResponseEntity.ok(Map.of("google_login_url", "/oauth2/authorization/google"));
    }

    @Operation(summary = "Solicita un reseteo de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud recibida (Email enviado si el usuario existe)"),
            @ApiResponse(responseCode = "400", description = "Email mal formado"),
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        try {
            authService.forgotPassword(request);
        } catch (RuntimeException e) {
            // No revelamos si el usuario no fue encontrado
        }
        return ResponseEntity.ok(Map.of("message", "Si tu email está registrado, recibirás un enlace para resetear tu contraseña."));
    }

    @Operation(summary = "Confirma un reseteo de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido, expirado o contraseña corta"),
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "¡Contraseña actualizada exitosamente!"));
    }

    // --- ¡El endpoint register-refugio YA SE HABÍA QUITADO DE AQUÍ! ---
}