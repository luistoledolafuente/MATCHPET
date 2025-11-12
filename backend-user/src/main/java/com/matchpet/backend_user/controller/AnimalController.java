package com.matchpet.backend_user.controller;

// DTOs
import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;

// Modelos y Servicios
import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.service.AnimalService;

// Anotaciones de Spring y Swagger
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

import java.security.Principal; // ¡Importante para saber QUIÉN está logueado!

@RestController
@RequestMapping("/api/animales") // <-- ¡La ruta base para todo el CRUD de Animales!
@RequiredArgsConstructor
@Tag(name = "4. Gestión de Animales", description = "Endpoints para el CRUD de animales del refugio")
public class AnimalController {

    private final AnimalService animalService;

    /**
     * Endpoint para REGISTRAR un nuevo animal.
     * URL: POST http://localhost:8080/api/animales
     */
    @Operation(
            summary = "Registra un nuevo animal [HU-06]",
            description = "Permite a un usuario autenticado (con rol Refugio) registrar un nuevo animal en su perfil.",
            security = @SecurityRequirement(name = "bearerAuth") // ¡Endpoint protegido!
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Animal registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado (sin token)"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es un refugio)"),
            @ApiResponse(responseCode = "500", description = "Error interno (ej: ID de raza no encontrado)")
    })
    @PostMapping
    public ResponseEntity<AnimalDTO> createAnimal(
            @Valid @RequestBody CreateAnimalRequest request,
            Principal principal // <-- Spring inyecta aquí al usuario autenticado
    ) {
        // 1. Obtenemos el email (username) del refugio desde el token
        String userEmail = principal.getName();

        // 2. Llamamos al servicio para hacer el trabajo
        Animal animalCreado = animalService.createAnimal(request, userEmail);

        // 3. Convertimos la Entidad (Animal) a un DTO (AnimalDTO) para la respuesta
        AnimalDTO responseDTO = convertToDTO(animalCreado);

        // 4. Devolvemos un 201 Created (el estándar para POST exitosos)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    /**
     * Método privado para convertir la Entidad Animal a su DTO de respuesta.
     * Esto evita exponer la entidad completa y previene bucles infinitos.
     */
    private AnimalDTO convertToDTO(Animal animal) {
        return AnimalDTO.builder()
                .id(animal.getId())
                .nombre(animal.getNombre())
                .fechaNacimientoAprox(animal.getFechaNacimientoAprox())
                .descripcionPersonalidad(animal.getDescripcionPersonalidad())
                .compatibleNiños(animal.getCompatibleNiños())
                .compatibleOtrasMascotas(animal.getCompatibleOtrasMascotas())
                .estaVacunado(animal.getEstaVacunado())
                .estaEsterilizado(animal.getEstaEsterilizado())
                .historialMedico(animal.getHistorialMedico())
                .fechaIngresoRefugio(animal.getFechaIngresoRefugio())
                // --- Objetos completos (ya no son solo IDs) ---
                .raza(animal.getRaza())
                .genero(animal.getGenero())
                .tamano(animal.getTamano())
                .nivelEnergia(animal.getNivelEnergia())
                .estadoAdopcion(animal.getEstadoAdopcion())
                .temperamentos(animal.getTemperamentos())
                .fotos(animal.getFotos())
                .build();
    }
}