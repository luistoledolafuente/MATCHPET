package com.matchpet.backend_user.config;

import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.JwtService;
import jakarta.annotation.PostConstruct;
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

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${frontend.url}")
    private String frontendUrl;  // Esta propiedad debería estar correctamente inyectada

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nombre = (String) attributes.get("name");

        UserModel user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewGoogleUser(email, nombre));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Redirige al frontend URL después de iniciar sesión exitosamente
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Este es el lugar donde devuelves los tokens como JSON, si es necesario redirigir:
        // response.sendRedirect(frontendUrl + "/dashboard");  // Por ejemplo, redirigiendo al dashboard.

        response.getWriter().write(
                "{\"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\"}"
        );
        response.getWriter().flush();
    }

    private UserModel registerNewGoogleUser(String email, String nombreCompleto) {

        RolModel defaultRole = rolRepository.findByNombreRol("Adoptante")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Adoptante' no encontrado."));

        String randomPassword = UUID.randomUUID().toString();

        String[] nombreParts = nombreCompleto.split("\\s+", 3);
        String nombre = nombreParts.length > 0 ? nombreParts[0] : "Usuario";
        String apellidoPaterno = nombreParts.length > 1 ? nombreParts[1] : "";
        String apellidoMaterno = nombreParts.length > 2 ? nombreParts[2] : "";

        UserModel newUser = UserModel.builder()
                .email(email)
                .nombre(nombre)
                .apellidoPaterno(apellidoPaterno)
                .apellidoMaterno(apellidoMaterno)
                .hashContrasena(passwordEncoder.encode(randomPassword))
                .roles(Set.of(defaultRole))
                .estaActivo(true)
                .fechaCreacionPerfil(new Timestamp(System.currentTimeMillis()))
                .fechaActualizacion(new Timestamp(System.currentTimeMillis()))
                .telefono("000000000")
                .build();

        return userRepository.save(newUser);
    }
}
