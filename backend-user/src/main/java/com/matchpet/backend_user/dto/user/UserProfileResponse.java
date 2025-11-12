package com.matchpet.backend_user.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matchpet.backend_user.model.PerfilAdoptante;
import com.matchpet.backend_user.model.Refugio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set; // ¡Importante!

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private Integer usuarioId; // <-- Mantenemos tu nombre de campo
    private String email;
    private String nombreCompleto;
    private String telefono;

    // --- ¡NUEVOS CAMPOS PARA HU-07! ---
    private Set<String> roles;
    private Refugio refugio;
    private PerfilAdoptante adoptante;
    // --- FIN DE CAMPOS NUEVOS ---
}