package com.matchpet.backend_user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
                // CSRF OFF
                .csrf(AbstractHttpConfigurer::disable)

                // CORS
                .cors(Customizer.withDefaults())

                // Devuelve 401 si no hay token
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // AUTORIZACIONES
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()

                        // Ruta protegida obligatoria
                        .requestMatchers("/api/user/profile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/animales").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/animales/**").permitAll()

                        // RUTAS PÚBLICAS
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/animales/**",
                                "/api/adoptantes/register",
                                "/api/refugios/register",
                                "/api/lookups/**",
                                "/api/donaciones/checkout",
                                "/login/oauth2/**",
                                "/",
                                "/error"
                        ).permitAll()

                        // Todo lo demás protegerlo
                        .anyRequest().authenticated()
                )

                // sesión STATELESS
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // OAuth2
                .oauth2Login(oauth ->
                        oauth.successHandler(oAuth2LoginSuccessHandler))

                // provider
                .authenticationProvider(authenticationProvider())

                // filtro JWT
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // limpiar contexto al hacer logout
                .logout(l -> l.logoutSuccessHandler(
                        (request, response, authentication) -> SecurityContextHolder.clearContext()
                ));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));

        // PARA LEER EL TOKEN
        config.setExposedHeaders(Arrays.asList("Authorization"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
