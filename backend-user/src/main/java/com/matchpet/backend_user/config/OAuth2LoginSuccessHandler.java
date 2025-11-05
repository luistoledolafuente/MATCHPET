package com.matchpet.backend_user.config;

import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // --- Herramientas que necesitamos ---
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // (Descomenta para habilitar frontend y configurar las properties)
    // @Value("${frontend.url}")
    // private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Obtenemos los datos del usuario de Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nombre = (String) attributes.get("name");

        // 2. Buscamos al usuario en NUESTRA BD (o lo registramos)
        UserModel user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewGoogleUser(email, nombre));

        // 3. --- ¡FIX PARA HU-04! ---
        // Generamos AMBOS tokens (Access y Refresh)
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 4. (Configuración de frontend - ¡Actualizada!)
        // (Cuando tengas frontend, redirigirás con ambos tokens)
        // String redirectUrl = frontendUrl + "/login-success?accessToken=" + accessToken + "&refreshToken=" + refreshToken;
        // response.sendRedirect(redirectUrl);


        // 5. --- ¡FIX PARA PRUEBA SOLO BACKEND! ---
        // Escribimos ambos tokens como JSON en la respuesta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\"}"
        );
        response.getWriter().flush(); // Asegura que se envíe la respuesta
    }

    /**
     * Método privado para crear un nuevo usuario si no existe
     * (Este método no necesita cambios)
     */
    private UserModel registerNewGoogleUser(String email, String nombre) {

        RolModel defaultRole = rolRepository.findByNombreRol("Adoptante")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Adoptante' no encontrado."));

        String randomPassword = UUID.randomUUID().toString();

        UserModel newUser = UserModel.builder()
                .email(email)
                .nombreCompleto(nombre)
                .hashContrasena(passwordEncoder.encode(randomPassword))
                .roles(Set.of(defaultRole))
                .estaActivo(true)
                .fechaCreacionPerfil(new Timestamp(System.currentTimeMillis()))
                .fechaActualizacion(new Timestamp(System.currentTimeMillis()))
                .build();

        return userRepository.save(newUser);
    }
}
