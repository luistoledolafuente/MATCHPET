package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.AuthResponse;
import com.matchpet.backend_user.dto.RegisterRefugioRequest;
import com.matchpet.backend_user.dto.UpdateRefugioRequest;
import com.matchpet.backend_user.model.Refugio;

public interface RefugioService {

    /**
     * ¡NUEVO! Registra un nuevo Refugio en el sistema.
     * Crea un Usuario con rol 'Refugio' y un perfil de Refugio asociado.
     * @param request El DTO con los datos del nuevo usuario y el refugio.
     * @return Una respuesta con el token JWT.
     */
    AuthResponse registerRefugio(RegisterRefugioRequest request);

    /**
     * Actualiza el perfil de un refugio existente.
     *
     * @param refugioId El ID del refugio a actualizar.
     * @param request El DTO con la nueva información.
     * @return El refugio actualizado.
     */
    Refugio updateRefugio(Integer refugioId, UpdateRefugioRequest request);

}