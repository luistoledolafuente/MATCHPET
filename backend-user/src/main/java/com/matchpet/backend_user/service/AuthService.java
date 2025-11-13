package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.auth.*;

public interface AuthService {

    // ¡El método register() SE HA QUITADO DE AQUÍ!

    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);

    // ¡El método registerRefugio() YA SE HABÍA QUITADO DE AQUÍ!
}