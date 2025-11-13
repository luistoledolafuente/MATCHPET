package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.donacion.CheckoutResponseDTO;
import com.matchpet.backend_user.dto.donacion.CreateDonacionRequest;
import com.matchpet.backend_user.dto.donacion.DonacionResponseDTO;

import java.util.List;

public interface DonacionService {

    /**
     * Inicia el proceso de una nueva donación.
     * Crea la donación en estado "Pendiente" y devuelve una URL/ID de pasarela de pago.
     *
     * @param request El DTO con los detalles de la donación.
     * @param userEmail El email del usuario autenticado (si existe, puede ser nulo).
     * @return Un DTO con el ID de la sesión de checkout.
     */
    CheckoutResponseDTO createDonacion(CreateDonacionRequest request, String userEmail);

    /**
     * Obtiene todas las donaciones recibidas por un refugio específico.
     *
     * @param refugioEmail El email del usuario (Refugio) autenticado.
     * @return Una lista de DTOs de donaciones.
     */
    List<DonacionResponseDTO> getDonacionesByRefugio(String refugioEmail);

    /**
     * Obtiene todas las donaciones realizadas por un adoptante.
     *
     * @param adoptanteEmail El email del usuario (Adoptante) autenticado.
     * @return Una lista de DTOs de donaciones.
     */
    List<DonacionResponseDTO> getMisDonaciones(String adoptanteEmail);

    // Aquí podríamos añadir un método 'webhook' para que la pasarela de pago
    // nos avise cuando el pago sea exitoso (ej: 'updateEstadoPago')
}