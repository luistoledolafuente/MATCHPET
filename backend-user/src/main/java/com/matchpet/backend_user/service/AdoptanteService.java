package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.AuthResponse;
import com.matchpet.backend_user.dto.RegisterRequest;
import com.matchpet.backend_user.dto.UpdateAdoptanteRequest;
import com.matchpet.backend_user.dto.UserProfileResponse;

public interface AdoptanteService {

    /**
     * Registra un nuevo Adoptante en el sistema.
     * (Lógica movida desde AuthService)
     */
    AuthResponse registerAdoptante(RegisterRequest request);

    /**
     * Actualiza el perfil de un adoptante existente. [HU-05]
     */
    UserProfileResponse updateAdoptante(Integer usuarioId, UpdateAdoptanteRequest request);
}