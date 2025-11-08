package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.*;
import com.matchpet.backend_user.model.PasswordResetToken;
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.PasswordResetTokenRepository;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.AuthService;
import com.matchpet.backend_user.service.EmailService;
import com.matchpet.backend_user.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet; // <-- ¡Importante!
import java.util.Set;     // <-- ¡Importante!
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 1. Validar si el email ya existe
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("El email ya está registrado");
        });

        // 2. Buscar el rol por defecto
        RolModel defaultRole = rolRepository.findByNombreRol("Adoptante")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Adoptante' no encontrado."));

        // 3. Crear el nuevo usuario
        // --- ¡CORRECCIÓN AQUÍ! ---
        Set<RolModel> roles = new HashSet<>();
        roles.add(defaultRole);

        UserModel user = UserModel.builder()
                .email(request.getEmail())
                .hashContrasena(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .telefono(request.getTelefono())
                .roles(roles) // <-- Se usa el HashSet mutable
                .estaActivo(true)
                .fechaCreacionPerfil(new Timestamp(System.currentTimeMillis()))
                .fechaActualizacion(new Timestamp(System.currentTimeMillis()))
                .build();
        // --- FIN DE LA CORRECCIÓN ---

        // 4. Guardar en la BD
        userRepository.save(user);

        // 5. Generar los tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 6. Devolver la respuesta
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // (Sin cambios en este método)
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
        // (Sin cambios en este método)
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
        // (Sin cambios en este método)
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + request.getEmail()));
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        String tokenStr = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + 3600000;
        Timestamp expiryDate = new Timestamp(expiryTime);
        PasswordResetToken resetToken = new PasswordResetToken(tokenStr, expiryDate, user);
        tokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), tokenStr);
    }


    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // (Sin cambios en este método)
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token de reseteo inválido"));
        if (resetToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token de reseteo expirado");
        }
        UserModel user = resetToken.getUser();
        user.setHashContrasena(passwordEncoder.encode(request.getNewPassword()));
        user.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user); // Esta línea es la que fallaba
        tokenRepository.delete(resetToken);
    }
}