package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.*;


/**
 * Este es el "contrato" de nuestro servicio de autenticación.
 * Define QUÉ se puede hacer, pero no CÓMO se hace.
 */
public interface AuthService {
    /**
     * Registra un nuevo usuario en el sistema.
     * @param request El DTO con los datos del nuevo usuario.
     * @return Una respuesta con el token JWT.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Autentica un usuario existente.
     * @param request El DTO con el email y la contraseña.
     * @return Una respuesta con el token JWT si la autenticación es exitosa.
     */
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

     /**
     * Obtiene el perfil del usuario.
     * @param username El email o username del usuario.
     * @return Un DTO con la información del usuario.
     */
    UserProfileResponse getProfile(String username);

    /**
     * Inicia el proceso de reseteo de contraseña.
     * @param request El DTO que contiene el email del usuario.
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Completa el reseteo de contraseña.
     * @param request El DTO que contiene el token y la nueva contraseña.
     */
    void resetPassword(ResetPasswordRequest request);

    AuthResponse registerRefugio(RegisterRefugioRequest request);

}