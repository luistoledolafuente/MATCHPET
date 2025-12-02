package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.dto.adoptante.UpdateAdoptanteRequest;
import com.matchpet.backend_user.dto.user.UserProfileResponse;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.service.AdoptanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> updateAdoptanteProfile(
            @PathVariable Integer id,  // Aquí extraemos el id de la URL
            @AuthenticationPrincipal UserModel userDetails,
            @Valid @RequestBody UpdateAdoptanteRequest request
    ) {
        Integer authenticatedUserId = userDetails.getId();

        if (!authenticatedUserId.equals(id)) {
            return ResponseEntity.status(403).build(); // Responder con 403 si intenta modificar otro perfil
        }

        UserProfileResponse updatedProfile = adoptanteService.updateAdoptante(id, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @Operation(summary = "Adoptante: Añadir un animal a favoritos")
    @PostMapping("/favoritos/{animalId}")
    public ResponseEntity<Map<String, String>> addFavorite(
            @PathVariable Integer animalId,
            @AuthenticationPrincipal UserModel userDetails  // Obtener el usuario autenticado
    ) {
        // Verifica que el usuario esté autenticado
        String userEmail = userDetails.getEmail();

        // Llamar al servicio para añadir el animal a favoritos del usuario
        adoptanteService.addFavoriteAnimal(userEmail, animalId);

        // Respuesta exitosa
        return ResponseEntity.ok(Map.of("message", "Animal añadido a favoritos."));
    }

    @Operation(summary = "Adoptante: Eliminar un animal de favoritos")
    @DeleteMapping("/favoritos/{animalId}")
    public ResponseEntity<Map<String, String>> removeFavorite(
            @PathVariable Integer animalId,  // ID del animal a eliminar de favoritos
            @AuthenticationPrincipal UserModel userDetails  // Obtener el usuario autenticado
    ) {
        // Verifica que el usuario esté autenticado
        String userEmail = userDetails.getEmail();

        // Llamar al servicio para eliminar el animal de los favoritos del usuario
        adoptanteService.removeFavoriteAnimal(userEmail, animalId);

        // Respuesta exitosa
        return ResponseEntity.ok(Map.of("message", "Animal eliminado de favoritos."));
    }

    @Operation(summary = "Adoptante: Obtener la lista de animales favoritos")
    @GetMapping("/favoritos")
    public ResponseEntity<List<AnimalDTO>> getFavorites(@AuthenticationPrincipal UserModel userDetails) {
        // Verifica que el usuario esté autenticado
        String userEmail = userDetails.getEmail();

        // Llamar al servicio para obtener los animales favoritos del usuario
        List<AnimalDTO> favoritos = adoptanteService.getFavoriteAnimals(userEmail);

        // Responder con la lista de favoritos
        return ResponseEntity.ok(favoritos);
    }



}