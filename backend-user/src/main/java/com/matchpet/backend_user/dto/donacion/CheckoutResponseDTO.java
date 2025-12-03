package com.matchpet.backend_user.dto.donacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponseDTO {
    private String preferenceId; // El ID real de Mercado Pago (ej: "123456-abcdef...")
    private String url;          // La URL de pago (init_point)
    private Integer donacionId;  // ID interno de tu BD
}