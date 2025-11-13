package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.dto.donacion.CheckoutResponseDTO;
import com.matchpet.backend_user.dto.donacion.CreateDonacionRequest;
import com.matchpet.backend_user.dto.donacion.DonacionResponseDTO;
import com.matchpet.backend_user.service.DonacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/donaciones")
@RequiredArgsConstructor
@Tag(name = "6. Gestión de Donaciones", description = "Endpoints para crear y ver donaciones")
public class DonacionController {

    private final DonacionService donacionService;

    @Operation(
            summary = "Inicia un checkout para una nueva donación",
            description = "Crea una donación en estado 'Pendiente' y devuelve un ID de sesión de pasarela de pago simulado. " +
                    "Puede ser llamado por un usuario autenticado (Adoptante) o un invitado anónimo."
    )
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> createDonacion(
            @Valid @RequestBody CreateDonacionRequest request) {

        // Verificamos si el usuario está logueado o es un invitado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            userEmail = authentication.getName(); // Obtiene el email del usuario logueado
        }

        // El servicio maneja la lógica de si es invitado o no
        CheckoutResponseDTO response = donacionService.createDonacion(request, userEmail);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtiene las donaciones recibidas por el Refugio autenticado",
            description = "Devuelve una lista de todas las donaciones hechas a este refugio.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/recibidas")
    public ResponseEntity<List<DonacionResponseDTO>> getDonacionesRecibidas(Principal principal) {
        // Obtenemos el email del Refugio desde el token
        String refugioEmail = principal.getName();
        List<DonacionResponseDTO> donaciones = donacionService.getDonacionesByRefugio(refugioEmail);
        return ResponseEntity.ok(donaciones);
    }

    @Operation(
            summary = "Obtiene las donaciones realizadas por el Adoptante autenticado",
            description = "Devuelve un historial de todas las donaciones que ha hecho este adoptante.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/mis-donaciones")
    public ResponseEntity<List<DonacionResponseDTO>> getMisDonaciones(Principal principal) {
        // Obtenemos el email del Adoptante desde el token
        String adoptanteEmail = principal.getName();
        List<DonacionResponseDTO> donaciones = donacionService.getMisDonaciones(adoptanteEmail);
        return ResponseEntity.ok(donaciones);
    }

    // NOTA: En un proyecto real, aquí faltaría un endpoint público (Webhook)
    // @PostMapping("/webhook/pago-confirmado")
    // para que la pasarela de pago (Culqi/Stripe) nos avise
    // cuando una donación "Pendiente" pase a "Completado".
}