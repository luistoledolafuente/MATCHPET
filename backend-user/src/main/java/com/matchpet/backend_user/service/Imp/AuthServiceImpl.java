package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.auth.*;
import com.matchpet.backend_user.model.PasswordResetToken;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.PasswordResetTokenRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.AuthService;
import com.matchpet.backend_user.service.EmailService;
import com.matchpet.backend_user.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- Import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    // PasswordEncoder SÍ se necesita para resetPassword
    private final PasswordEncoder passwordEncoder;
    // --- FIN DE LA CORRECCIÓN ---

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    // El método register() se movió a AdoptanteServiceImpl
    // El método registerRefugio() se movió a RefugioServiceImpl

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String userEmail = jwtService.extractUsernameFromRefreshToken(request.getRefreshToken());
        UserModel user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String newAccessToken = jwtService.generateAccessToken(user);
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .build();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + request.getEmail()));

        // CAMBIO: Llama al nuevo método para crear y guardar el token en una transacción separada.
        String tokenStr = this.createAndSaveToken(user);

        // El email se envía. Si falla, el token ya está guardado.
        emailService.sendPasswordResetEmail(user.getEmail(), tokenStr);
    }

    // --- NUEVO MÉTODO PRIVADO (Añadir al final de la clase) ---
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected String createAndSaveToken(UserModel user) {

        // 1. Eliminar token anterior
        tokenRepository.findByUser(user).ifPresent(token -> {
            tokenRepository.delete(token);

            // 🚨 FIX CRÍTICO: Forzar a JPA a enviar el DELETE a la DB inmediatamente.
            tokenRepository.flush();
        });

        // 2. Crear nuevo token
        String tokenStr = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + 3600000;
        Timestamp expiryDate = new Timestamp(expiryTime);
        PasswordResetToken resetToken = new PasswordResetToken(tokenStr, expiryDate, user);

        // 3. Guardar. El INSERT ya no encontrará un duplicado.
        tokenRepository.save(resetToken);

        return tokenStr;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token de reseteo inválido"));
        if (resetToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token de reseteo expirado");
        }
        UserModel user = resetToken.getUser();

        // Esta línea ahora funcionará
        user.setHashContrasena(passwordEncoder.encode(request.getNewPassword()));
        user.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}