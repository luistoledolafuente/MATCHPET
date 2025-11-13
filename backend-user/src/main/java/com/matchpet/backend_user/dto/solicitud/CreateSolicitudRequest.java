package com.matchpet.backend_user.dto.solicitud;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSolicitudRequest {

    @NotNull(message = "El ID del animal es requerido")
    private Integer animalId;

    // (Podríamos añadir un "mensaje" opcional aquí si quisiéramos)
}