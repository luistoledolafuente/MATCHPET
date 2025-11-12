package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.user.UserProfileResponse;


public interface UserService {

    /**
     * Obtiene el perfil del usuario actualmente autenticado.
     * @return Un DTO seguro con la información del perfil.
     */
    UserProfileResponse getUserProfile();
}