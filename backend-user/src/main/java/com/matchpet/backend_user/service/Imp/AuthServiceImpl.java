package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.AuthResponse;
import com.matchpet.backend_user.dto.LoginRequest;
import com.matchpet.backend_user.dto.RegisterRequest;
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.AuthService;
import com.matchpet.backend_user.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.matchpet.backend_user.dto.RefreshTokenRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        UserModel user = UserModel.builder()
                .email(request.getEmail())
                .hashContrasena(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .telefono(request.getTelefono())
                .roles(Set.of(defaultRole))
                .estaActivo(true)
                .fechaCreacionPerfil(new Timestamp(System.currentTimeMillis()))
                .fechaActualizacion(new Timestamp(System.currentTimeMillis()))
                .build();

        // 4. Guardar en la BD
        userRepository.save(user);

        // 5. Generar los tokens (¡FIX!)
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 6. Devolver la respuesta (¡FIX!)
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar al usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Si la autenticación es exitosa, buscar al usuario
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));

        // 3. Generar los tokens (¡FIX!)
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 4. Devolver la respuesta (¡FIX!)
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

        // ⚡ Este nuevo access token ahora siempre será único
        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .build();
    }

}