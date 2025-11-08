package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.*; // Importa TODOS los DTOs
import com.matchpet.backend_user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importa GetMapping
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") //
@RequiredArgsConstructor
@Tag(name = "1. Autenticación", description = "Endpoints para Registro y Login") //
public class AuthController {

    private final AuthService authService; //

    // ... (tus endpoints register, login, refreshToken y google-login-url se quedan igual)
    @Operation(summary = "Registra un nuevo usuario") //
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente (devuelve token)"), //
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: email mal formado, pass corto)"), //
            @ApiResponse(responseCode = "500", description = "Error interno (ej: email ya existe)") //
    })
    @PostMapping("/register") //
    public ResponseEntity<AuthResponse> register( //
                                                  @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request)); //
    }


    @PostMapping("/login") //
    public ResponseEntity<AuthResponse> login( //
                                               @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request)); //
    }

    @Operation(summary = "Renueva un token de acceso (Refresh Token)") //
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Token de acceso renovado exitosamente"), //
            @ApiResponse(responseCode = "403", description = "Refresh Token inválido o expirado") //
    })
    @PostMapping("/refresh") //
    public ResponseEntity<AuthResponse> refreshToken( //
                                                      @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request)); //
    }

    @Operation( //
            summary = "Obtener URL para Login con Google (Solo Documentación)", //
            description = "¡ESTE ENDPOINT NO SE LLAMA DIRECTAMENTE! \n\n" + //
                    "Es solo para documentación. \n\n" + //
                    "Para iniciar el login con Google, el frontend debe **redirigir** al usuario (con un link `<a>` o un `window.location.href`) " + //
                    "a la URL que se devuelve en la respuesta: `/oauth2/authorization/google`" //
    )
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Devuelve la URL a la que el frontend debe redirigir al usuario") //
    })
    @GetMapping("/google-login-url") //
    public ResponseEntity<Map<String, String>> getGoogleLoginUrl() {
        // Devolvemos la URL mágica que creó Spring Security
        return ResponseEntity.ok(Map.of("google_login_url", "/oauth2/authorization/google")); //
    }

    // --- ¡NUEVOS ENDPOINTS! ---

    @Operation(summary = "Solicita un reseteo de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud recibida (Email enviado si el usuario existe)"),
            @ApiResponse(responseCode = "400", description = "Email mal formado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado (Aunque por seguridad, devolvemos 200)")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        // Por seguridad, NUNCA le decimos al frontend si el email fue encontrado o no.
        // Simplemente aceptamos la solicitud y, si el usuario existe, enviamos el email.
        try {
            authService.forgotPassword(request);
        } catch (RuntimeException e) {
            // Capturamos el "Usuario no encontrado" pero no hacemos nada.
            // En un caso real, aquí podrías loggear el error internamente.
        }

        // Devolvemos una respuesta genérica
        return ResponseEntity.ok(Map.of("message", "Si tu email está registrado, recibirás un enlace para resetear tu contraseña."));
    }


    @Operation(summary = "Confirma un reseteo de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido o contraseña corta"),
            @ApiResponse(responseCode = "400", description = "Token expirado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        // A diferencia del forgot-password, aquí sí queremos que el usuario
        // sepa si el token es inválido o expiró, para que pueda pedir uno nuevo.
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "¡Contraseña actualizada exitosamente!"));
    }
}