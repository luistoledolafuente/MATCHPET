package com.matchpet.backend_user.dto.solicitud;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.adoptante.AdoptanteInfoDTO;
import com.matchpet.backend_user.model.lookup.EstadoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

// Este DTO representa una solicitud "completa" para mostrar en el frontend
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponseDTO {

    // Datos de la solicitud
    private Integer id;
    private Timestamp fechaSolicitud;
    private Timestamp fechaActualizacion;
    private String notasInternas;
    private String mensajeAlAdoptante;

    // Datos del estado
    private EstadoSolicitud estadoSolicitud;

    // --- Objetos Anidados ---

    // El animal por el que se está aplicando
    private AnimalDTO animal;

    // El adoptante que está aplicando
    private AdoptanteInfoDTO adoptante;
}