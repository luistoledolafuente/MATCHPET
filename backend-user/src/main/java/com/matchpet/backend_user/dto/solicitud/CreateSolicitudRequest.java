package com.matchpet.backend_user.dto.solicitud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSolicitudRequest {

    @NotNull(message = "El ID del animal es requerido")
    private Integer animalId;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensajeAdoptante; // En lugar de 'mensaje'
}