package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.dto.animal.UpdateAnimalRequest;
import com.matchpet.backend_user.service.AnimalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.matchpet.backend_user.service.RecomendacionService; // ¡NUEVO!
import java.security.Principal;

import java.security.Principal;
import java.util.List;
import java.util.Map; // <-- ¡NUEVO IMPORT!

@RestController
@RequestMapping("/api/animales")
@RequiredArgsConstructor
@Tag(name = "4. Gestión de Animales", description = "Endpoints para el CRUD de animales del refugio")
public class AnimalController {

    private final AnimalService animalService;
    private final RecomendacionService recomendacionService;


    @Operation(
            summary = "Registra un nuevo animal [HU-06 Create]",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Animal registrado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es un refugio)")
    })
    @PostMapping
    public ResponseEntity<AnimalDTO> createAnimal(
            @Valid @RequestBody CreateAnimalRequest request,
            Principal principal
    ) {
        String userEmail = principal.getName();
        AnimalDTO responseDTO = animalService.createAnimal(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @Operation(
            summary = "Obtiene los animales del refugio autenticado [HU-06 Read]",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de animales devuelta exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es un refugio)")
    })
    @GetMapping("/mis-animales")
    public ResponseEntity<List<AnimalDTO>> getMisAnimales(Principal principal) {
        String userEmail = principal.getName();
        List<AnimalDTO> misAnimales = animalService.getAnimalesByRefugio(userEmail);
        return ResponseEntity.ok(misAnimales);
    }


    @Operation(
            summary = "Actualiza un animal existente [HU-06 Update]",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal actualizado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado (el animal no pertenece a este refugio)"),
            @ApiResponse(responseCode = "404", description = "Animal no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AnimalDTO> updateAnimal(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAnimalRequest request,
            Principal principal
    ) {
        String userEmail = principal.getName();
        AnimalDTO animalActualizado = animalService.updateAnimal(id, request, userEmail);
        return ResponseEntity.ok(animalActualizado);
    }


    // --- ¡NUEVO ENDPOINT PARA DELETE (HU-06)! ---
    @Operation(
            summary = "Elimina un animal existente [HU-06 Delete]",
            description = "Permite al refugio autenticado eliminar uno de sus animales, identificado por su ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Animal eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (el animal no pertenece a este refugio)"),
            @ApiResponse(responseCode = "404", description = "Animal no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAnimal(
            @PathVariable Integer id, // <-- El ID del animal a eliminar
            Principal principal // <-- El usuario refugio autenticado
    ) {
        // 1. Obtenemos el email del refugio (para seguridad)
        String userEmail = principal.getName();

        // 2. Llamamos al servicio para que haga el borrado
        animalService.deleteAnimal(id, userEmail);

        // 3. Devolvemos una respuesta simple de éxito
        // (DELETE también puede devolver 204 No Content, pero un 200 con JSON es más claro)
        return ResponseEntity.ok(Map.of("message", "Animal eliminado exitosamente"));
    }

    @Operation(
            summary = "Obtiene recomendaciones de mascotas (Match IA)",
            description = "Utiliza el perfil del adoptante autenticado para obtener una lista de mascotas recomendadas por la IA.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/recomendados")
    public ResponseEntity<List<AnimalDTO>> getRecomendaciones(Principal principal) {

        String adoptanteEmail = principal.getName();
        List<AnimalDTO> recomendaciones = recomendacionService.getRecomendaciones(adoptanteEmail);

        return ResponseEntity.ok(recomendaciones);
    }
}