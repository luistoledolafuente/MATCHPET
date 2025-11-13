package com.matchpet.backend_user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

// --- Imports de tus DTOs (¡AHORA CON LA RUTA CORRECTA!) ---
import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.auth.LoginRequest;
import com.matchpet.backend_user.dto.auth.RefreshTokenRequest;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest; // <-- RUTA CORREGIDA
import com.matchpet.backend_user.dto.refugio.RegisterRefugioRequest; // <-- RUTA CORREGIDA

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFullAuthenticationFlow() throws Exception {
        String uniqueEmail = "test-" + System.currentTimeMillis() + "@matchpet.com";
        String password = "password123";
        String nombre = "Test User Flow";

        // --- ¡NOMBRE DE CLASE CORREGIDO! ---
        RegisterAdoptanteRequest registerRequest = new RegisterAdoptanteRequest();
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword(password);
        registerRequest.setNombreCompleto(nombre);
        registerRequest.setTelefono("999888777");

        // --- ¡CORRECCIÓN DE RUTA! ---
        mockMvc.perform(post("/api/adoptantes/register") // <-- RUTA ACTUALIZADA
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk()) // Ahora SÍ esperará 200
                .andExpect(jsonPath("$.accessToken").exists());

        // (Login y GetProfile no cambian)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(jsonResponse, AuthResponse.class);
        String accessToken = authResponse.getAccessToken();

        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").exists())
                .andExpect(jsonPath("$.adoptante").exists());
    }

    @Test
    public void testRefreshTokenFlow_Success() throws Exception {
        String uniqueEmail = "test-refresh-" + System.currentTimeMillis() + "@matchpet.com";
        String password = "password123";

        // --- ¡NOMBRE DE CLASE CORREGIDO! ---
        RegisterAdoptanteRequest registerRequest = new RegisterAdoptanteRequest();
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword(password);
        registerRequest.setNombreCompleto("Test Refresh");

        // --- ¡CORRECCIÓN DE RUTA! ---
        mockMvc.perform(post("/api/adoptantes/register") // <-- RUTA ACTUALIZADA
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk()); // Ahora SÍ esperará 200

        // (El resto del test no cambia)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(jsonResponse, AuthResponse.class);
        String refreshToken = authResponse.getRefreshToken();
        String oldAccessToken = authResponse.getAccessToken();

        Thread.sleep(100);

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(refreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshJsonResponse = refreshResult.getResponse().getContentAsString();
        AuthResponse refreshAuthResponse = objectMapper.readValue(refreshJsonResponse, AuthResponse.class);
        String newAccessToken = refreshAuthResponse.getAccessToken();

        assertNotNull(newAccessToken);
        assertNotEquals(oldAccessToken, newAccessToken);
    }

    @Test
    public void testRefreshTokenFlow_Fail_BadToken() throws Exception {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("un.token.falso.y.malo");

        // --- ¡CORRECCIÓN DE TEST! ---
        // Esperamos 401 Unauthorized, que es lo que devuelve nuestro GlobalExceptionHandler
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized()); // <-- CAMBIADO DE 403 a 401
    }


    @Test
    public void testRegisterRefugioFlow_Success() throws Exception {
        String loginEmail = "refugio-login-" + System.currentTimeMillis() + "@matchpet.com";
        String refugioEmail = "refugio-public-" + System.currentTimeMillis() + "@matchpet.com";
        String password = "passwordRefugio123";

        RegisterRefugioRequest request = RegisterRefugioRequest.builder()
                .emailLogin(loginEmail)
                .password(password)
                .nombreRefugio("Refugio Patitas Felices Test")
                .direccion("Av. Siempre Viva 123")
                .ciudad("Springfield")
                .emailRefugio(refugioEmail)
                .personaContacto("Ana Test")
                .telefonoContacto("987654321")
                .build();

        // --- ¡CORRECCIÓN DE RUTA! ---
        mockMvc.perform(post("/api/refugios/register") // <-- RUTA ACTUALIZADA
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // <-- Aquí sí esperamos 200 OK
                .andExpect(jsonPath("$.accessToken").exists());
    }
}