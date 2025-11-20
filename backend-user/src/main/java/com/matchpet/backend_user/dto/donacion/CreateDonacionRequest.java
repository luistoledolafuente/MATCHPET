package com.matchpet.backend_user.dto.donacion;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDonacionRequest {

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "1.00", message = "La donación mínima es de 1.00")
    private BigDecimal monto;

    @NotBlank(message = "La moneda es requerida (ej: 'PEN')")
    private String moneda;

    // --- Datos del Donante (si es invitado) ---
    // Si el usuario está logueado, ignoramos estos campos.
    private String nombreDonante;
    private String emailDonante;

    // --- Destino de la Donación (Opcional) ---
    private Integer refugioId;  // Donación directa a un refugio
    private Integer animalId;   // Donación directa a un animal

    private String mensajeDonante; // Mensaje opcional
}