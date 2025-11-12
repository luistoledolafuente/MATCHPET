package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.solicitud.CreateSolicitudRequest;
import com.matchpet.backend_user.dto.solicitud.SolicitudResponseDTO;
import com.matchpet.backend_user.dto.solicitud.UpdateSolicitudRequest;
import com.matchpet.backend_user.service.SolicitudAdopcionService;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@Tag(name = "5. Gestión de Solicitudes", description = "Endpoints para crear y gestionar solicitudes de adopción")
@SecurityRequirement(name = "bearerAuth") // ¡Todos los endpoints aquí son protegidos!
public class SolicitudAdopcionController {

    private final SolicitudAdopcionService solicitudService;

    // --- ENDPOINTS PARA ADOPTANTES ---

    @Operation(summary = "Adoptante: Enviar una solicitud de adopción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Animal o Usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> createSolicitud(
            @Valid @RequestBody CreateSolicitudRequest request,
            Principal principal // Spring nos da el usuario autenticado
    ) {
        String adoptanteEmail = principal.getName();
        SolicitudResponseDTO solicitudCreada = solicitudService.createSolicitud(request, adoptanteEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada);
    }

    @Operation(summary = "Adoptante: Ver mi historial de solicitudes enviadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<SolicitudResponseDTO>> getMisSolicitudes(Principal principal) {
        String adoptanteEmail = principal.getName();
        List<SolicitudResponseDTO> misSolicitudes = solicitudService.getMisSolicitudes(adoptanteEmail);
        return ResponseEntity.ok(misSolicitudes);
    }

    // --- ENDPOINTS PARA REFUGIOS ---

    @Operation(summary = "Refugio: Ver todas las solicitudes recibidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes recibidas"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (el usuario no es un refugio)")
    })
    @GetMapping("/recibidas")
    public ResponseEntity<List<SolicitudResponseDTO>> getSolicitudesRecibidas(Principal principal) {
        String refugioEmail = principal.getName();
        List<SolicitudResponseDTO> solicitudesRecibidas = solicitudService.getSolicitudesRecibidas(refugioEmail);
        return ResponseEntity.ok(solicitudesRecibidas);
    }

    @Operation(summary = "Refugio: Actualizar estado de una solicitud (Aprobar/Rechazar)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud actualizada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es un refugio o la solicitud no es suya)"),
            @ApiResponse(responseCode = "404", description = "Solicitud o Estado no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> updateSolicitud(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSolicitudRequest request,
            Principal principal
    ) {
        String refugioEmail = principal.getName();
        SolicitudResponseDTO solicitudActualizada = solicitudService.updateSolicitud(id, request, refugioEmail);
        return ResponseEntity.ok(solicitudActualizada);
    }
}