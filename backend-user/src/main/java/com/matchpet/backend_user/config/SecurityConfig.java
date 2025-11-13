package com.matchpet.backend_user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer; 
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; 
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder; // <-- ¡IMPORTACIÓN AÑADIDA!
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler; 

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. Configurar CORS
            .cors(Customizer.withDefaults()) 
            
            // 3. Manejo de excepciones (para que devuelva 401 Unauthorized si no hay token)
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

            // 4. Autorización de solicitudes:
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()

                // Volvemos a proteger /api/user/profile:
                .requestMatchers("/api/user/profile").authenticated()
                
                // Rutas públicas
                .requestMatchers(
                    "/api/auth/**",
                    "/api/adoptantes/register",
                    "/api/refugios/register"
                ).permitAll()
                
                // CUALQUIER otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            // 5. Gestión de sesión (CRÍTICO para JWT: debe ser STATELESS)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 6. Configurar el proveedor de autenticación
            .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder))
            
            // 7. Añadir el filtro JWT antes del filtro estándar de login/usuario
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // 8. CRÍTICO: Limpiar el contexto de seguridad al salir.
            .logout(l -> l.logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext())));


        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * Configuración CORS. Spring Security detecta y usa este filtro.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 1. Permite Credenciales (necesario para JWT en el header)
        config.setAllowCredentials(true);
        // 2. Orígenes permitidos (tu frontend React)
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        // 3. Permitir todos los Headers y Métodos
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("*"));
        
        // 4. CRÍTICO: Exponer el header Authorization para que React lo pueda leer.
        config.setExposedHeaders(Arrays.asList("Authorization")); 

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}