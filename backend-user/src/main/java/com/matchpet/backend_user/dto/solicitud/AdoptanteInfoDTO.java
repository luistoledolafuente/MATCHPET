package com.matchpet.backend_user.dto.adoptante;

import com.matchpet.backend_user.model.PerfilAdoptante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Este DTO representa la información pública de un adoptante
// para mostrarla al refugio en una solicitud.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptanteInfoDTO {

    private Integer usuarioId;
    private String email;
    private String nombreCompleto;
    private String telefono;

    // También adjuntamos su perfil (dirección, ciudad, etc.)
    private PerfilAdoptante perfil;
}