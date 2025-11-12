package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.AuthResponse;
import com.matchpet.backend_user.dto.RegisterRefugioRequest;
import com.matchpet.backend_user.dto.UpdateRefugioRequest;
import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.service.RefugioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refugios") // ¡Ruta base para todo lo de refugios!
@RequiredArgsConstructor
@Tag(name = "3. Gestión de Refugios", description = "Endpoints para crear y actualizar perfiles de refugios")
public class RefugioController {

    private final RefugioService refugioService;

    // --- ¡NUEVO ENDPOINT MOVIDO AQUÍ! ---
    @Operation(summary = "Registra un nuevo Refugio [H-5]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refugio registrado exitosamente (devuelve token)"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno (ej: email ya existe)")
    })
    @PostMapping("/register") // La nueva URL será: POST /api/refugios/register
    public ResponseEntity<AuthResponse> registerRefugio(
            @Valid @RequestBody RegisterRefugioRequest request
    ) {
        return ResponseEntity.ok(refugioService.registerRefugio(request));
    }


    // --- ENDPOINT QUE YA TENÍAMOS ---
    @Operation(
            summary = "Actualiza el perfil de un refugio [HU-06]",
            description = "Permite a un usuario autenticado (con rol de Refugio) actualizar sus propios datos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refugio actualizado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Refugio no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Refugio> updateRefugioProfile(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRefugioRequest request
    ) {
        Refugio refugioActualizado = refugioService.updateRefugio(id, request);
        return ResponseEntity.ok(refugioActualizado);
    }

}