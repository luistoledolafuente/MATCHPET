package com.matchpet.backend_user.dto.donacion;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO para devolver la información de la pasarela de pago al frontend
@Data
@AllArgsConstructor
public class CheckoutResponseDTO {

    private String checkoutSessionId; // El ID de la sesión de pago (ej: de Culqi)
    private Integer donacionId;       // El ID de la donación que acabamos de crear en BD
}