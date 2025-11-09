package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.*; // Asegúrate de que importe todos los DTOs
import com.matchpet.backend_user.service.AuthService;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "Registra un nuevo usuario (Adoptante)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente (devuelve token)"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno (ej: email ya existe)")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Endpoint para INICIAR SESIÓN.
     * URL: POST http://localhost:8080/api/auth/login
     */
    @Operation(summary = "Inicia sesión (Login)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ... (Tus otros endpoints: /refresh, /google-login-url, /forgot-password, /reset-password se quedan igual)

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

    @GetMapping("/profile")
        public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UserProfileResponse profile = authService.getProfile(authentication.getName());
        return ResponseEntity.ok(profile);
        }


    // --- ¡NUEVO ENDPOINT PARA H-5! ---

    @Operation(summary = "Registra un nuevo Refugio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refugio registrado exitosamente (devuelve token)"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: email mal formado, pass corto)"),
            @ApiResponse(responseCode = "500", description = "Error interno (ej: email ya existe, rol no encontrado)")
    })
    @PostMapping("/register-refugio")
    public ResponseEntity<AuthResponse> registerRefugio(
            @Valid @RequestBody RegisterRefugioRequest request
    ) {
        // Llamamos al nuevo método del servicio que creamos en el Paso 2
        return ResponseEntity.ok(authService.registerRefugio(request));
    }
    // --- FIN DEL NUEVO ENDPOINT ---

}