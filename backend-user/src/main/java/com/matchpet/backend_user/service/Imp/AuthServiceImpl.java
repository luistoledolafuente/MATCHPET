package com.matchpet.backend_user.service.Imp;

// --- ¡IMPORTS AÑADIDOS! ---
import com.matchpet.backend_user.dto.RegisterRefugioRequest;
import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.repository.RefugioRepository;
// --- FIN DE IMPORTS AÑADIDOS ---

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
import java.util.HashSet;
import java.util.Set;
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

    // --- ¡INYECCIÓN AÑADIDA! ---
    // Este era el primer error que marcaba la imagen
    private final RefugioRepository refugioRepository;
    // --- FIN DE LA INYECCIÓN ---


    @Override
    public AuthResponse register(RegisterRequest request) {
        // (Tu método register normal... sin cambios)
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("El email ya está registrado");
        });
        RolModel defaultRole = rolRepository.findByNombreRol("Adoptante")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Adoptante' no encontrado."));
        Set<RolModel> roles = new HashSet<>();
        roles.add(defaultRole);
        UserModel user = UserModel.builder()
                .email(request.getEmail())
                .hashContrasena(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .telefono(request.getTelefono())
                .roles(roles)
                .estaActivo(true)
                .fechaCreacionPerfil(new Timestamp(System.currentTimeMillis()))
                .fechaActualizacion(new Timestamp(System.currentTimeMillis()))
                .build();
        userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // ... (Tus métodos login, refreshToken, forgotPassword y resetPassword se quedan igual)

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
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token de reseteo inválido"));
        if (resetToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token de reseteo expirado");
        }
        UserModel user = resetToken.getUser();
        user.setHashContrasena(passwordEncoder.encode(request.getNewPassword()));
        user.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }

    // --- ¡MÉTODO GET PROFILE CORREGIDO! ---
        @Override
        @Transactional // <-- ¡CRUCIAL! Esto resuelve el 500 de Lazy Initialization
        public UserProfileResponse getProfile(String username) {
        UserModel user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener el rol principal (asumiendo que tiene al menos uno)
        // CRUCIAL: El front-end necesita saber si es "Adoptante" o "Refugio" para el dashboard
        String mainRole = user.getRoles().stream()
                .findFirst() // Toma el primer rol (siempre debería tener al menos uno)
                .map(RolModel::getNombreRol)
                .orElse("ADOPTANTE"); // Falla por defecto a Adoptante si no hay rol

        return UserProfileResponse.builder()
                .usuarioId(user.getId())
                .nombreCompleto(user.getNombreCompleto())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .role(mainRole) // <-- ¡Añadimos el rol aquí!
                .build();
        }

    // --- ¡NUEVO MÉTODO CORREGIDO! ---
    // (Este es el método que estabas escribiendo)
    @Override
    @Transactional
    public AuthResponse registerRefugio(RegisterRefugioRequest request) {

        // 1. Validar que el email de LOGIN no exista
        userRepository.findByEmail(request.getEmailLogin()).ifPresent(user -> {
            throw new RuntimeException("El email de login ya está registrado");
        });

        // 2. Validar que el email del REFUGIO no exista
        refugioRepository.findByEmail(request.getEmailRefugio()).ifPresent(refugio -> {
            throw new RuntimeException("El email del refugio ya está registrado");
        });

        // 3. Buscar el rol "Refugio"
        RolModel refugioRole = rolRepository.findByNombreRol("Refugio")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Refugio' no encontrado."));

        // 4. Crear la entidad Refugio
        Refugio nuevoRefugio = new Refugio();
        nuevoRefugio.setNombre(request.getNombreRefugio());
        nuevoRefugio.setDireccion(request.getDireccion());
        nuevoRefugio.setCiudad(request.getCiudad());
        nuevoRefugio.setEmail(request.getEmailRefugio());
        nuevoRefugio.setPersonaContacto(request.getPersonaContacto());
        nuevoRefugio.setTelefono(request.getTelefonoContacto());

        // 5. Crear la entidad Usuario
        Set<RolModel> roles = new HashSet<>();
        roles.add(refugioRole);

        UserModel user = UserModel.builder()
                .email(request.getEmailLogin())
                .hashContrasena(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getPersonaContacto())
                .telefono(request.getTelefonoContacto())
                .roles(roles)
                .estaActivo(true)
                .fechaCreacionPerfil(new Timestamp(System.currentTimeMillis()))
                .fechaActualizacion(new Timestamp(System.currentTimeMillis()))
                .refugio(nuevoRefugio) // <-- ¡Ahora esto funcionará!
                .build();

        // 6. Guardar el Usuario
        userRepository.save(user);

        // 7. Generar los tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 8. Devolver la respuesta
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}