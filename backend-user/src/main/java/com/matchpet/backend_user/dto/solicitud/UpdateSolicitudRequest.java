package com.matchpet.backend_user.dto.solicitud;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSolicitudRequest {

    @NotNull(message = "El ID del nuevo estado es requerido")
    private Integer estadoSolicitudId; // Ej: 3 (Aprobada), 4 (Rechazada)

    private String notasInternas; // Opcional: Notas privadas para el refugio

    private String mensajeAlAdoptante; // Opcional: Mensaje que verá el adoptante
}