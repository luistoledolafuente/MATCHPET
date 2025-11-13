package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.dto.adoptante.UpdateAdoptanteRequest;
import com.matchpet.backend_user.dto.user.UserProfileResponse;
import com.matchpet.backend_user.service.AdoptanteService;
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
@RequestMapping("/api/adoptantes") // <-- ¡Nueva ruta base!
@RequiredArgsConstructor
@Tag(name = "2. Gestión de Adoptantes", description = "Endpoints para crear y actualizar perfiles de adoptantes")
public class AdoptanteController {

    private final AdoptanteService adoptanteService;

    @Operation(summary = "Registra un nuevo usuario (Adoptante)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente (devuelve token)"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno (ej: email ya existe)")
    })
    @PostMapping("/register") // La nueva URL será: POST /api/adoptantes/register
    public ResponseEntity<AuthResponse> registerAdoptante(
            @Valid @RequestBody RegisterAdoptanteRequest request
    ) {
        return ResponseEntity.ok(adoptanteService.registerAdoptante(request));
    }

    @Operation(
            summary = "Actualiza el perfil de un adoptante [HU-05]",
            description = "Permite a un usuario autenticado (con rol de Adoptante) actualizar sus propios datos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}") // La URL será: PUT /api/adoptantes/{id}
    public ResponseEntity<UserProfileResponse> updateAdoptanteProfile(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAdoptanteRequest request
    ) {
        // TODO: Añadir seguridad para que un usuario solo pueda editar SU PROPIO perfil.
        UserProfileResponse updatedProfile = adoptanteService.updateAdoptante(id, request);
        return ResponseEntity.ok(updatedProfile);
    }
}